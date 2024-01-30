package med.voll.api.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.pacientes.DadosCadastroPaciente;
import med.voll.api.pacientes.Paciente;
import med.voll.api.pacientes.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/pacientes")
public class PacienteController {
    @Autowired
    private PacienteRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<Paciente> cadastrar(@RequestBody @Valid  DadosCadastroPaciente dados) {
        Paciente paciente = repository.save(new Paciente(dados));
        return ResponseEntity.ok().body(paciente);
    }
}
