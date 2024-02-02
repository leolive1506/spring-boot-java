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
## [JPA Query Methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
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

# Error
- não incluir stacktrace
```properties
server.error.include-stacktrace=never
```
- Personalizar retorno exception
```java
@RestControllerAdvice
public class TratadorErros {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarErro404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErroValidacao(MethodArgumentNotValidException exception) {
        var errors = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(errors.stream().map(DadosErroValidacao::new).toList());
    }

    public record DadosErroValidacao(
            String campo,
            String mensagem
    ) {
        public DadosErroValidacao(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
```
- Bean validation consegue traduzir as mensagem automaticamente passando parametro no header Accept-Language
![img.png](img.png)
## Personalizando mensagens de erro
- adicionando o atributo message nas próprias anotações de validação:

```java
public record DadosCadastroMedico(
        @NotBlank(message = "Nome é obrigatório")
        String nome
) {}
```
- isolando as mensagens em um arquivo de propriedades
  - que deve possuir o nome ValidationMessages.properties e ser criado no diretório src/main/resources:
```properties
nome.obrigatorio=Nome é obrigatório
```
```java
public record DadosCadastroMedico(
    @NotBlank(message = "{nome.obrigatorio}")
    String nome
) {}
```

# Spring security
1. autenticação
1. autorização (controle de acesso)
3. Proteção contra ataques (CSRF, clickjacking, etc)

## Diferneça statefull x stateless
1. Statefull (Autenticação em aplicações web)
- Session guardando estado do usuário
2. Stateless (Autenticação em API Rest)
- API Rest não deve guardar estados
  - servidor processa, devolve resposta e na próxima requisição não tem sessão
  - JWT (JSON web tokens)

## Autenticação
1. requisição com dados de login
2. API verifica se dados são validos (verifica database)
3. Gera JWT
4. Devolve JWT

## Validação token
1. Request enviando JWT
2. Valição JWT
3. Token válido? (se não bloqueia requisição)

## Funcionamento
So de adicionar depencia, ao iniciar projeto
  - cria uma senha pra ambiente de desenvolvimento ao iniciar projeto 
  - bloqueia todas requisições por padrão e redireciona para uma tela de login
    - Cria um usuário default com nome 'user' e password no log do console (ideal para aplicações statefull)
    - Spring security permite personalizar isso
```xml
<dependencies>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
  </dependency>
</dependencies>
```

## Implementação em API Rest
- Ao implementar não tem comportamento padrão e fornecer tela de login e bloquear todas requisições 
```java
@Service
public class AutenticacaoService implements UserDetailsService {
    @Autowired
    private UsuarioRepository repository;

    // loadUserByUsername - metodo que spring chama automaticamente ao fazer login
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }
}
```
```java
@Configuration
@EnableWebSecurity // indicar que irá personalizar configurações de segurança
public class SecurityConfigurations {

    // devolve o retorno do metodo
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
```

# Dicas spring
## @Bean
Exportar uma classe para o spring, fazendo com que ele consiga carregá-la e realize a sua injeção em outras classes

# Links
- [Trello com as funcionalidades](https://trello.com/b/O0lGCsKb/api-voll-med)
- [explicação CORS](https://cursos.alura.com.br/course/spring-boot-3-desenvolva-api-rest-java/task/116048)
- [Explicação classes recodd](https://cursos.alura.com.br/course/spring-boot-3-desenvolva-api-rest-java/task/116049)