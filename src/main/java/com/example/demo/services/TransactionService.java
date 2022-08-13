package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.demo.models.AccountModel;
import com.example.demo.models.Respuestas;
import com.example.demo.models.TransactionModel;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransactionService {
    public List<AccountModel> accounts = new ArrayList<AccountModel>();
    public List<TransactionModel> transactions = new ArrayList<TransactionModel>();

    public List<Respuestas> executeTransaction(String listTransction){
        List<Respuestas> resp = new ArrayList<Respuestas>();
        try {
            JSONArray data = new JSONArray(listTransction);
            ObjectMapper objectMapper = new ObjectMapper();

            for(Object obj: data){
                JSONObject item = (JSONObject)obj;
                if(item.has("transaction")){
                    resp.add(executeTransaction(objectMapper.readValue(item.get("transaction").toString(), TransactionModel.class)));
                }else if(item.has("account")){
                    resp.add(addAccount(objectToAccount(item.getJSONObject("account"))));
                }
            }
        } catch (Exception e) {
            Respuestas respError = new Respuestas();
            List<String> validation = new ArrayList<String>();
            validation.add("invalid-json-object");
            respError.setViolations(validation);
            resp.add(respError);
        }
        return resp;
    }

    private AccountModel objectToAccount(JSONObject obj){
        AccountModel account = new AccountModel();
        account.setActive_card(obj.getBoolean("active-card"));
        account.setAvailable_limit(obj.getInt("available-limit"));
        account.setId(obj.getLong("id"));
        return account;
    }

    private AccountModel findAccountById(long id) {
        for(AccountModel item : accounts) {
            if(item.getId() == id) {
                return new AccountModel(item);
            }
        }
        return null;
    }

    private void setAccount(AccountModel account) {
        for(AccountModel item : accounts) {
            if(item.getId() == account.getId()) {
                item.setAvailable_limit(account.getAvailable_limit());
                break;
            }
        }
    }

    private Respuestas addAccount(AccountModel account){
        Respuestas resp = new Respuestas();
        List<String> validation = new ArrayList<String>();
        resp.setAccount(findAccountById(account.getId()));
        if(resp.getAccount() == null){
            accounts.add(account);
            resp.setAccount(new AccountModel(account));
            resp.setViolations(validation);
        }else{
            validation.add("account-already-initialized");
            resp.setViolations(validation);
        }
        return resp;
    }

    private Respuestas executeTransaction(TransactionModel transaction){
        Respuestas resp = new Respuestas();
        List<String> validation = new ArrayList<String>();
        resp.setAccount(findAccountById(transaction.getId()));
        if(resp.getAccount() == null){
            validation.add("account-not-initialized");
            resp.setViolations(validation);
        }else if(resp.getAccount() != null && !resp.getAccount().getActive_card()){
            validation.add("card-not-active");
            resp.setViolations(validation);
        }else if(resp.getAccount().getAvailable_limit() < transaction.getAmount() ){
            validation.add("insufficient-limit");
            resp.setViolations(validation);
        }else{
            transactions.add(transaction);
            resp.getAccount().setAvailable_limit(resp.getAccount().getAvailable_limit() - transaction.getAmount());
            setAccount(resp.getAccount());
        }
        return resp;
    }
}
