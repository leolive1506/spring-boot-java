package med.voll.api.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.pacientes.DadosAtualizacaoPaciente;
import med.voll.api.pacientes.DadosCadastroPaciente;
import med.voll.api.pacientes.Paciente;
import med.voll.api.pacientes.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/pacientes")
public class PacienteController {
    @Autowired
    private PacienteRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<Paciente> cadastrar(@RequestBody @Valid DadosCadastroPaciente dados) {
        Paciente paciente = repository.save(new Paciente(dados));
        return ResponseEntity.ok().body(paciente);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoPaciente dados) {
        Paciente paciente = repository.getReferenceById(id);
        paciente.atualizar(dados);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<Paciente>> listar(@PageableDefault Pageable paginacao) {
        Page<Paciente> pacientes = repository.findAllByAtivoTrue(paginacao);
        return ResponseEntity.ok().body(pacientes);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Paciente paciente = repository.getReferenceById(id);
        paciente.excluir();
        return ResponseEntity.noContent().build();
    }
}
