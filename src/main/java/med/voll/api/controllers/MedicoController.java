package med.voll.api.controllers;

import jakarta.validation.Valid;
import med.voll.api.medicos.DadosCadastroMedico;
import med.voll.api.medicos.DadosListagemMedico;
import med.voll.api.medicos.Medico;
import med.voll.api.medicos.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<Medico> cadastrar(@RequestBody @Valid DadosCadastroMedico dados) {
        Medico medico = repository.save(new Medico(dados));
        return ResponseEntity.ok().body(medico);
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemMedico>> listar() {
        List<Medico> medicos = repository.findAll();
        return ResponseEntity.ok().body(
            medicos.stream().map(DadosListagemMedico::new).toList()
        );
    }
}
