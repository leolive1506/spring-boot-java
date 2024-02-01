package med.voll.api.domain.medicos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import med.voll.api.domain.enderecos.DadosEndereco;

public record DadosCadastroMedico(
        @NotBlank // não nulo e nem vazio
    String nome,
        @NotBlank
    @Email
    String email,
        @NotBlank
    String telefone,
        @NotBlank
    @Pattern(regexp = "\\d{4,6}")
    String crm,
        @NotNull // não é not blank, pois not blank é so pra string
    Especialidade especialidade,
        @NotNull
    @Valid DadosEndereco endereco //  valid diz que endereco tem validcoes e pra validar
) {
}
