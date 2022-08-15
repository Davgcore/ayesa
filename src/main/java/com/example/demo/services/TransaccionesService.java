package com.example.demo.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.demo.models.CuentaModel;
import com.example.demo.models.Respuestas;
import com.example.demo.models.TransaccionModel;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransaccionesService {
    public List<CuentaModel> cuentas = new ArrayList<CuentaModel>();
    public List<TransaccionModel> transacciones = new ArrayList<TransaccionModel>();

    public List<Respuestas> doTransaccion(String listTransction){
        List<Respuestas> resp = new ArrayList<Respuestas>();
        try {
            JSONArray data = new JSONArray(listTransction);
            ObjectMapper objectMapper = new ObjectMapper();

            for(Object obj: data){
                JSONObject item = (JSONObject)obj;
                if(item.has("transaction")){
                    resp.add(addTransaccion(objectMapper.readValue(item.get("transaction").toString(), TransaccionModel.class)));
                }else if(item.has("account")){
                    resp.add(addAccount(castToCuenta(item.getJSONObject("account"))));
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

    private CuentaModel castToCuenta(JSONObject obj){
        CuentaModel account = new CuentaModel();
        account.setActive_card(obj.getBoolean("active-card"));
        account.setAvailable_limit(obj.getInt("available-limit"));
        account.setId(obj.getLong("id"));
        return account;
    }

    private CuentaModel findAccountById(long id) {
        for(CuentaModel item : cuentas) {
            if(item.getId() == id) {
                return new CuentaModel(item);
            }
        }
        return null;
    }

    private void setAccount(CuentaModel account) {
        for(CuentaModel item : cuentas) {
            if(item.getId() == account.getId()) {
                item.setAvailable_limit(account.getAvailable_limit());
                break;
            }
        }
    }

    private Respuestas addAccount(CuentaModel account){
        Respuestas resp = new Respuestas();
        List<String> validation = new ArrayList<String>();
        resp.setAccount(findAccountById(account.getId()));
        if(resp.getAccount() == null){
            cuentas.add(account);
            resp.setAccount(new CuentaModel(account));
            resp.setViolations(validation);
        }else{
            validation.add("account-already-initialized");
            resp.setViolations(validation);
        }
        return resp;
    }

    private Respuestas addTransaccion(TransaccionModel transaction){
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
        }else if (validarDosTransaccionesIguales_DosMin(transaction)){
            validation.add("doubled-transaction");
            resp.setViolations(validation);
        }else if (validarTresTransacciones_DosMin(transaction)){
            validation.add("high-frequency-small-interval");
            resp.setViolations(validation);
        }else{
            transacciones.add(transaction);
            resp.getAccount().setAvailable_limit(resp.getAccount().getAvailable_limit() - transaction.getAmount());
            setAccount(resp.getAccount());
        }
        return resp;
    }

    private boolean validarTresTransacciones_DosMin(TransaccionModel transaction){
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(transaction.getTime());
        endDate.add(Calendar.MINUTE,2);

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(transaction.getTime());
        startDate.add(Calendar.MINUTE, -2);

        List<TransaccionModel> list = transacciones.stream().filter(t -> !t.getTime().after(endDate.getTime()) && !t.getTime().before(startDate.getTime())).collect(Collectors.toList());

        return list.size() >= 3 ;
    }

    private boolean validarDosTransaccionesIguales_DosMin(TransaccionModel transaccion){
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(transaccion.getTime());
        endDate.add(Calendar.MINUTE,2);

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(transaccion.getTime());
        startDate.add(Calendar.MINUTE, -2);

        List<TransaccionModel> list = transacciones.stream().filter(t -> !t.getTime().after(endDate.getTime()) && !t.getTime().before(startDate.getTime()) && t.getMerchant().equals(transaccion.getMerchant())).collect(Collectors.toList());

        return list.size() >= 1 ;
    }
}
