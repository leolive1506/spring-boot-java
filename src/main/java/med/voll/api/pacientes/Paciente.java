package med.voll.api.pacientes;

import jakarta.persistence.*;
import lombok.*;
import med.voll.api.enderecos.Endereco;

@Table(name = "pacientes")
@Entity(name = "Paciente")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private Boolean ativo;

    @Embedded
    private Endereco endereco;

    public Paciente(DadosCadastroPaciente paciente) {
        nome = paciente.nome();
        email = paciente.email();
        cpf = paciente.cpf();
        telefone = paciente.telefone();
        endereco = new Endereco(paciente.endereco());
        this.ativo = true;
    }

    public void atualizar(DadosAtualizacaoPaciente dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }

        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }

        if (dados.endereco() != null) {
            this.endereco.atualizarInformacoes(dados.endereco());
        }
    }

    public void excluir()
    {
        this.ativo = false;
    }
}
