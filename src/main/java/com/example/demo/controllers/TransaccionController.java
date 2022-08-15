package com.example.demo.controllers;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Respuestas;
import com.example.demo.services.TransaccionesService;

@RestController
public class TransaccionController {
    TransaccionesService transaccionService = new TransaccionesService();
    @PostMapping("/transaccion")
    public ResponseEntity<List<Respuestas>> request(@RequestBody String input){
        return ResponseEntity.ok().body(transaccionService.doTransaccion(input));
    }

    @GetMapping("/")
    public ResponseEntity<String> getRequest(){
        return ResponseEntity.ok("Hola Mundo");
    }
}
