package com.example.demo.models;

import java.util.List;

public class Respuestas {
    private AccountModel account;
    private List<String> violations;
    
    public AccountModel getAccount() {
        return account;
    }
    public void setAccount(AccountModel account) {
        this.account = account;
    }
    public List<String> getViolations() {
        return violations;
    }
    public void setViolations(List<String> violations) {
        this.violations = violations;
    }
    
}
