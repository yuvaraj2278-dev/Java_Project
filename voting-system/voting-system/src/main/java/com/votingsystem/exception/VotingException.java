package com.votingsystem.exception;

/** Business rule violation (election closed, invalid candidate, ...). */
public class VotingException extends RuntimeException {
    public VotingException(String message) { super(message); }
}
