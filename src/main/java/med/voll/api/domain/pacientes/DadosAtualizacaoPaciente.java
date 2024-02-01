package med.voll.api.domain.pacientes;

import jakarta.validation.Valid;
import med.voll.api.domain.enderecos.DadosEndereco;

public record DadosAtualizacaoPaciente(
        String nome,
        String telefone,
        @Valid DadosEndereco endereco
) {

}
