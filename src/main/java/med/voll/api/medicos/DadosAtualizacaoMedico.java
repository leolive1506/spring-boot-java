package med.voll.api.medicos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import med.voll.api.enderecos.DadosEndereco;

public record DadosAtualizacaoMedico(
    String nome,
    String telefone,
    @Valid DadosEndereco endereco
) {
}
