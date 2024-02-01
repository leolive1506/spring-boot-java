package med.voll.api.controllers;

import jakarta.validation.Valid;
import med.voll.api.medicos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoMedico> cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) {
        Medico medico = repository.save(new Medico(dados));
        var uri = uriBuilder.path("/medicos/{id}")
                    .buildAndExpand(medico.getId())
                    .toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    //    {{ _.baseURL }}/medicos?size=1&page=0&sort=id,desc
    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        Page<Medico> medicos = repository.findAllByAtivoTrue(paginacao);
        return ResponseEntity.ok().body(medicos.map(DadosListagemMedico::new));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoMedico> atualizar(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoMedico dados) {
        Medico medico = repository.getReferenceById(id);
        medico.atualizarInformacoes(dados);
        return ResponseEntity.ok().body(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Medico medico = repository.getReferenceById(id);
        medico.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoMedico> detalhar(@PathVariable Long id) {
        Medico medico = repository.getReferenceById(id);

        return ResponseEntity.ok().body(new DadosDetalhamentoMedico(medico));
    }
}
