package med.voll.api.domain.medicos;

import jakarta.validation.Valid;
import med.voll.api.domain.enderecos.DadosEndereco;

public record DadosAtualizacaoMedico(
    String nome,
    String telefone,
    @Valid DadosEndereco endereco
) {
}
