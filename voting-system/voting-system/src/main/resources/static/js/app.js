const $ = s => document.querySelector(s);
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const fmt = d => new Date(d).toLocaleString([], {dateStyle: 'medium', timeStyle: 'short'});
let timer = null, selectedCandidate = null;

async function api(path, opts = {}) {
  const res = await fetch('/api' + path, {headers: {'Content-Type': 'application/json'}, ...opts});
  if (res.status === 204) return null;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || 'Request failed');
  return data;
}

function toast(msg, error = false) {
  const t = $('#toast');
  t.textContent = msg; t.className = 'show' + (error ? ' error' : '');
  setTimeout(() => t.className = '', 3000);
}

/* ---------- Tabs ---------- */
document.querySelectorAll('.tab').forEach(b => b.onclick = () => showTab(b.dataset.tab));
function showTab(name) {
  document.querySelectorAll('.tab').forEach(t => t.classList.toggle('active', t.dataset.tab === name));
  document.querySelectorAll('.panel').forEach(p => p.classList.toggle('active', p.id === name));
  clearInterval(timer);
  if (name === 'elections') loadElections();
  if (name === 'results') { loadResultSelect(); timer = setInterval(loadResults, 5000); }
  if (name === 'admin') loadAdmin();
}

/* ---------- Elections ---------- */
async function loadElections() {
  const q = new URLSearchParams({category: $('#category').value, search: $('#search').value});
  const list = await api('/elections?' + q);
  let voted = [];
  const vid = $('#myVoterId').value.trim();
  if (vid) { try { voted = await api(`/voters/${encodeURIComponent(vid)}/voted`); } catch (e) {} }
  $('#electionGrid').innerHTML = list.map(e => {
    const has = voted.includes(e.id);
    return `<div class="card">
      <span class="badge ${e.status}">${e.status}</span><span class="cat">${esc(e.category)}</span>
      <h3>${esc(e.title)}</h3><p>${esc(e.description)}</p>
      <div class="meta">🗓 ${fmt(e.startTime)} → ${fmt(e.endTime)}<br>👥 ${e.candidates.length} candidates</div>
      <div class="actions">
        <button class="btn" ${e.status !== 'ACTIVE' || has ? 'disabled' : ''} onclick="openVote(${e.id})">${has ? '✔ Voted' : 'Vote now'}</button>
        <button class="btn ghost" onclick="goResults(${e.id})">Results</button>
      </div></div>`;
  }).join('') || '<p class="muted">No elections found.</p>';
}
['search', 'category'].forEach(id => $('#' + id).addEventListener('input', loadElections));
$('#myVoterId').addEventListener('change', () => { localStorage.setItem('voterId', $('#myVoterId').value.trim()); loadElections(); });

/* ---------- Voting ---------- */
async function openVote(id) {
  const e = await api('/elections/' + id);
  selectedCandidate = null;
  $('#voteBody').innerHTML = `
    <h2>${esc(e.title)}</h2><p class="muted">Select one candidate. Your vote is final.</p>
    ${e.candidates.map(c => `<label class="cand" onclick="pick(${c.id}, this)">
      <input type="radio" name="cand"><span class="em">${c.emoji}</span>
      <div><b>${esc(c.name)}</b><br><small>${esc(c.party)}</small></div></label>`).join('')}
    <label>Voter ID<input id="vVoter" value="${esc($('#myVoterId').value)}" placeholder="VT-XXXXXX"></label>
    <div class="actions"><button class="btn" onclick="castVote(${e.id})">Cast vote</button>
    <button class="btn ghost" onclick="voteModal.close()">Cancel</button></div>`;
  $('#voteModal').showModal();
}
function pick(id, el) {
  selectedCandidate = id;
  document.querySelectorAll('.cand').forEach(x => x.classList.remove('sel'));
  el.classList.add('sel');
}

async function castVote(electionId) {
  if (!selectedCandidate) return toast('Please select a candidate', true);
  try {
    const r = await api('/votes', {method: 'POST', body: JSON.stringify({
      voterId: $('#vVoter').value.trim(), electionId, candidateId: selectedCandidate})});
    $('#voteBody').innerHTML = `<h2 class="ok">✅ Vote recorded</h2>
      <div class="receipt"><div class="muted">Your receipt code</div><div class="code">${r.receiptCode}</div>
      <div>${esc(r.electionTitle)}</div><div class="muted">${fmt(r.castAt)}</div></div>
      <p class="muted">Keep this code to verify your ballot was counted.</p>
      <div class="actions"><button class="btn" onclick="window.print()">Print</button>
      <button class="btn ghost" onclick="voteModal.close();loadElections()">Close</button></div>`;
  } catch (err) { toast(err.message, true); }
}

/* ---------- Results ---------- */
async function loadResultSelect(selectId) {
  const list = await api('/elections');
  const sel = $('#resultSelect'), cur = selectId || sel.value;
  sel.innerHTML = list.map(e => `<option value="${e.id}">${esc(e.title)} (${e.status})</option>`).join('');
  if (cur) sel.value = cur;
  loadResults();
}
$('#resultSelect').onchange = loadResults;
function goResults(id) { showTab('results'); loadResultSelect(id); }

async function loadResults() {
  const id = $('#resultSelect').value; if (!id) return;
  const r = await api(`/elections/${id}/results`);
  $('#resultBox').innerHTML = `<h2>${esc(r.title)} <span class="badge ${r.status}">${r.status}</span></h2>
    <p class="muted">Total votes: <b>${r.totalVotes}</b></p>` +
    r.results.map((c, i) => `<div class="bar-row ${i === 0 && c.votes > 0 ? 'lead' : ''}">
      <div class="bar-head"><span>${c.emoji} ${esc(c.name)} <small class="muted">${esc(c.party)}</small></span>
      <span>${c.votes} · ${c.percentage}%</span></div>
      <div class="bar"><i style="width:${c.percentage}%"></i></div></div>`).join('');
}

/* ---------- Register / Verify ---------- */
$('#regForm').onsubmit = async ev => {
  ev.preventDefault();
  try {
    const v = await api('/voters/register', {method: 'POST', body: JSON.stringify({name: $('#regName').value, email: $('#regEmail').value})});
    localStorage.setItem('voterId', v.voterId); $('#myVoterId').value = v.voterId;
    $('#regOut').innerHTML = `<div class="receipt"><div class="muted">Your Voter ID</div><div class="code">${v.voterId}</div>
      <div class="muted">Saved in this browser. Keep it safe.</div></div>`;
  } catch (e) { toast(e.message, true); }
};

$('#verifyForm').onsubmit = async ev => {
  ev.preventDefault();
  try {
    const r = await api('/votes/verify/' + encodeURIComponent($('#receiptInput').value.trim()));
    $('#verifyOut').innerHTML = `<p class="ok">✅ Valid ballot recorded for <b>${esc(r.election)}</b><br><span class="muted">${fmt(r.castAt)}</span></p>`;
  } catch (e) { $('#verifyOut').innerHTML = `<p class="err">❌ ${esc(e.message)}</p>`; }
};

/* ---------- Admin ---------- */
async function loadAdmin() {
  const [s, list] = await Promise.all([api('/stats'), api('/elections')]);
  $('#statCards').innerHTML = [['Elections', s.totalElections], ['Active now', s.activeElections],
    ['Registered voters', s.registeredVoters], ['Votes cast', s.totalVotes]]
    .map(([l, v]) => `<div class="stat"><b>${v}</b><span>${l}</span></div>`).join('');
  $('#adminElections').innerHTML = list.map(e => `<div class="list-row"><span>${esc(e.title)} <span class="badge ${e.status}">${e.status}</span></span>
    <span class="actions">${e.status !== 'CLOSED' ? `<button class="btn ghost sm" onclick="closeElection(${e.id})">Close</button>` : ''}
    <button class="btn danger sm" onclick="deleteElection(${e.id})">Delete</button></span></div>`).join('');
  $('#auditLog').innerHTML = s.recentVotes.map(v => `<div class="list-row"><span>${v.receiptCode} · ${esc(v.electionTitle)}</span>
    <span class="muted">${v.voter} · ${fmt(v.castAt)}</span></div>`).join('') || '<p class="muted">No votes yet.</p>';
}
async function closeElection(id) { await api(`/elections/${id}/close`, {method: 'PUT'}); toast('Election closed'); loadAdmin(); }
async function deleteElection(id) {
  if (!confirm('Delete this election and all its votes?')) return;
  await api('/elections/' + id, {method: 'DELETE'}); toast('Election deleted'); loadAdmin();
}
$('#createForm').onsubmit = async ev => {
  ev.preventDefault();
  try {
    await api('/elections', {method: 'POST', body: JSON.stringify({
      title: $('#cTitle').value, description: $('#cDesc').value, category: $('#cCat').value,
      startTime: $('#cStart').value, endTime: $('#cEnd').value,
      candidates: $('#cCands').value.split('\n').filter(l => l.trim())})});
    ev.target.reset(); toast('Election created'); loadAdmin();
  } catch (e) { toast(e.message, true); }
};

/* ---------- Init ---------- */
(function init() {
  $('#myVoterId').value = localStorage.getItem('voterId') || '';
  const now = new Date(), pad = n => String(n).padStart(2, '0');
  const local = d => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  $('#cStart').value = local(now); $('#cEnd').value = local(new Date(now.getTime() + 7 * 864e5));
  loadElections();
})();
