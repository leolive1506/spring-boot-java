# Paginação
```java
    //    {{ _.baseURL }}/medicos?size=1&page=0
    //    {{ _.baseURL }}/medicos?sort=id,desc
    //    public ResponseEntity<Page<DadosListagemMedico>> listar(Pageable paginacao) {
    @GetMapping
    public ResponseEntity<Page<DadosListagemMedico>> listar(@PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        Page<Medico> medicos = repository.findAll(paginacao);
        return ResponseEntity.ok().body(medicos.map(DadosListagemMedico::new));
    }
```
- por padrão, os parâmetros utilizados para realizar a paginação e a ordenação são **page, size e sort**
  - modificar o nome padrão
```properties
spring.data.web.pageable.page-parameter=pagina
spring.data.web.pageable.size-parameter=tamanho
spring.data.web.sort.sort-parameter=ordem
```
# Validações (starter validation)
Modulo que integra com especificação java BeanValidation
- [Lista de anotations](https://jakarta.ee/specifications/bean-validation/3.0/jakarta-bean-validation-spec-3.0.html#builtinconstraints)
```java
public record DadosCadastroMedico(
    @NotBlank // não nulo e nem vazio
    String nome,
    @NotBlank
    @Email
    String email,
    @NotBlank
    @Pattern(regexp = "\\d{4,6}")
    String crm,
    @NotNull // não é not blank, pois not blank é so pra string
    Especialidade especialidade,
    @NotNull
    @Valid  DadosEndereco endereco //  valid diz que endereco tem validcoes e pra validar
) {
}
```
# Banco mysql
Configurações
```shell
spring.datasource.url=jdbc:mysql://localhost/vollmed_api
spring.datasource.username=root
spring.datasource.root=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
- habilitar log das queries
```properties
spring.jpa.show-sql=true
```
## Flyway
Adiciona migrations ao projeto
- cadança executada será criado arquivo .sql e lá escrever bloco de comando que será executado
- salvar em main/resources/db/migration
- para projeto durante a criação do arquivo
- segue um padrão de nomenclatura dos arquivos
  - V1__descricao.sql
### Erro por ter executado migration antecipadamente ao salvar arquivo
É importante parar o projeto ao criar os arquivos de migrations, para evitar que o Flyway os execute antes da hora, com o código ainda incompleto
- Esse erro também pode acontecer se o código da migration estiver inválido, contendo algum trecho de SQL digitado de maneira incorreta.
- Para resolver esse problema será necessário acessar o banco de dados da aplicação e executar o seguinte comando sql:
  - apagar na tabela do Flyway todas as migrations cuja execução falhou. Após isso, basta corrigir o código da migration e executar novamente o projeto.
```sql
delete from flyway_schema_history where success = 0;
```
- **Obs:** Pode acontecer de alguma migration ter criado uma tabela e/ou colunas e com isso o problema vai persistir, pois o flyway não vai apagar as tabelas/colunas criadas em migrations que falharam
  - Nesse caso você pode apagar o banco de dados e criá-lo novamente:
```sql
drop database vollmed_api;
create database vollmed_api; 
```

# CORS (Cross-Origin Resource Sharing - "compartilhamento de recursos com origens diferentes")
- informam aos navegadores para permitir que uma aplicação Web seja executada em uma origem e acesse recursos de outra origem diferente.
  - chamada de requisição cross-****origin HTTP
- informa aos navegadores se um determinado recurso pode ou não ser acessado.

## Same-origin policy
- uma aplicação Front-end, escrita em JavaScript, só consegue acessar recursos localizados na mesma origem da solicitação
- mecanismo de segurança dos Browsers que restringe a maneira de um documento ou script de uma origem interagir com recursos de outra origem
- política possui o objetivo de frear ataques maliciosos.
- Ao enviar uma requisição para uma API de origem diferente, a API precisa retornar um header chamado Access-Control-Allow-Origin
  - Dentro dele, informar as diferentes origens que serão permitidas para consumir a API
    - Access-Control-Allow-Origin: http://localhost:3000.
    - É possível permitir o acesso de qualquer origem utilizando o símbolo *(asterisco)
      - Access-Control-Allow-Origin: *
    - Mas isso não é uma medida recomendada, pois permite que origens desconhecidas acessem o servidor, a não ser que seja intencional, como no caso de uma API pública
## Habilitando diferentes origens no Spring Boot
```java
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT");
    }
}
```



# Links
- [Trello com as funcionalidades](https://trello.com/b/O0lGCsKb/api-voll-med)
- [explicação CORS](https://cursos.alura.com.br/course/spring-boot-3-desenvolva-api-rest-java/task/116048)
- [Explicação classes recodd](https://cursos.alura.com.br/course/spring-boot-3-desenvolva-api-rest-java/task/116049)