package com.example.demo.models;

import java.util.List;

public class Respuestas {
    private CuentaModel account;
    private List<String> violations;
    
    public CuentaModel getAccount() {
        return account;
    }
    public void setAccount(CuentaModel account) {
        this.account = account;
    }
    public List<String> getViolations() {
        return violations;
    }
    public void setViolations(List<String> violations) {
        this.violations = violations;
    }
    
}
