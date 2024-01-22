package med.voll.api.controllers;

import med.voll.api.medicos.DadosCadastroMedico;
import med.voll.api.medicos.Medico;
import med.voll.api.medicos.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<Medico> cadastrar(@RequestBody DadosCadastroMedico dados) {
        Medico medico = repository.save(new Medico(dados));
        return ResponseEntity.ok().body(medico);
    }
}
