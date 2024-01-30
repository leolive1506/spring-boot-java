package med.voll.api.pacientes;

import jakarta.validation.Valid;
import med.voll.api.enderecos.DadosEndereco;

public record DadosAtualizacaoPaciente(
        String nome,
        String telefone,
        @Valid DadosEndereco endereco
) {

}
