# ProspEco API - Sistema de Gerenciamento de Consumo de Energia Residencial

---

## Índice
1. [Visão Geral do Projeto](#visão-geral-do-projeto)  
2. [Melhorias Implementadas](#melhorias-implementadas)  
3. [Arquitetura](#arquitetura)  
4. [Recursos Principais](#recursos-principais)  
5. [Tecnologias Utilizadas](#tecnologias-utilizadas)  
6. [Padrões de Projeto Implementados](#padrões-de-projeto-implementados)  
7. [Endpoints da API](#endpoints-da-api)  
8. [Funcionalidades da Interface MVC](#funcionalidades-da-interface-mvc)  
9. [Instalação e Configuração](#instalação-e-configuração)  
10. [Links de Vídeos](#links-de-vídeos)  
11. [Como Contribuir](#como-contribuir)  
12. [Autores](#autores)  

---

## Visão Geral do Projeto

O **ProspEco API** é uma aplicação completa desenvolvida para promover a gestão eficiente de consumo energético em residências. A plataforma combina **APIs RESTful robustas** e uma interface **MVC intuitiva** para monitorar o consumo de aparelhos, definir metas de economia, gerenciar conquistas, enviar notificações e oferecer recomendações personalizadas baseadas em **inteligência artificial**.  
O projeto visa não apenas otimizar o consumo energético, mas também engajar os usuários por meio de um sistema de gamificação e relatórios detalhados.

---

## Melhorias Implementadas

### Funcionalidades Adicionadas na Sprint Atual
1. **Autenticação Segura:** Utilização de **JWT** para controle de acesso e autenticação.  
2. **Mensageria Kafka:** Processamento assíncrono para notificações e atualização de dados.  
3. **Monitoramento Avançado:** Integração com **Spring Boot Actuator** para métricas detalhadas e monitoramento da saúde do sistema.  
4. **Geração de Insights Personalizados:** Uso de **Spring AI** para gerar recomendações específicas baseadas no padrão de consumo do usuário.  

---

## Arquitetura

A arquitetura do **ProspEco API** segue um modelo multicamadas, promovendo organização, escalabilidade e manutenção eficiente.  

### Principais Camadas
1. **Camada de Apresentação (MVC):**  
   - Renderização dinâmica de páginas utilizando **Thymeleaf**.  
   - Interface de fácil uso para visualização e interação com dados.  

2. **Camada de Negócios (Serviços):**  
   - Centralização da lógica do sistema.  
   - Comunicação via **Kafka** para processamento de eventos em tempo real.  

3. **Camada de Persistência (Repositórios):**  
   - Gerenciamento de entidades e operações de banco de dados com **Spring Data JPA**.  

4. **Integração com Kafka:**  
   - Suporte à mensageria para notificações e atualização de status em segundo plano.  

5. **HATEOAS na API:**  
   - Melhoria na descoberta e navegação entre endpoints.  

---

## Recursos Principais

- **Gestão de Aparelhos:** Cadastro, edição, listagem e remoção de dispositivos.  
- **Monitoramento do Consumo:** Registro e acompanhamento detalhado do consumo energético.  
- **Definição de Metas:** Estabelecimento de objetivos de economia e acompanhamento de progresso.  
- **Recomendações Personalizadas:** Geração dinâmica de dicas para economia de energia.  
- **Gamificação:** Sistema de conquistas para motivar os usuários.  
- **Notificações Personalizadas:** Envio de alertas com base no consumo e nas metas configuradas.  
- **Monitoramento do Sistema:** Métricas e saúde da aplicação acessíveis em tempo real.  

---

## Tecnologias Utilizadas

- **Java 21**  
- **Spring Framework:** Boot, Data JPA, Security, Kafka, Actuator e AI.  
- **MySQL Azure:** Banco de dados relacional.  
- **Docker:** Contêinerização para simplificar a implantação.  
- **Thymeleaf:** Template engine para a camada MVC.  
- **Swagger/OpenAPI:** Documentação interativa para a API RESTful.  
- **Lombok:** Redução de boilerplate no código.  
- **Kafka:** Comunicação assíncrona e mensageria.  
- **Spring AI:** Para integração com modelos avançados de inteligência artificial.  

---

## Padrões de Projeto Implementados

- **DTO (Data Transfer Object):** Organização e eficiência na troca de dados entre camadas.  
- **Builder:** Facilitação na criação de objetos complexos.  
- **Singleton:** Utilizado para configuração de componentes como Kafka Producer e Consumer.  
- **Repository Pattern:** Estruturação do acesso a dados.  
- **Strategy:** Configuração flexível para autenticação e autorização de usuários.  

---

## Endpoints da API

### **Usuário**
- `GET /api/usuarios/{id}`: Obter informações de um usuário específico.  
- `PUT /api/usuarios/{id}`: Atualizar dados do usuário.  
- `DELETE /api/usuarios/{id}`: Remover usuário.  
- `GET /api/usuarios`: Listar todos os usuários.  
- `POST /api/usuarios`: Criar um novo usuário.

---

### **Registro de Consumo**
- `GET /api/registro-consumo/{id}`: Obter detalhes de um registro de consumo.  
- `PUT /api/registro-consumo/{id}`: Atualizar registro existente.  
- `DELETE /api/registro-consumo/{id}`: Excluir registro.  
- `POST /api/registro-consumo`: Criar um novo registro de consumo.  
- `GET /api/registro-consumo/aparelho/{aparelhoId}`: Listar registros de consumo associados a um aparelho específico.

---

### **Metas**
- `GET /api/metas/{id}`: Obter meta específica.  
- `PUT /api/metas/{id}`: Atualizar meta.  
- `DELETE /api/metas/{id}`: Excluir meta.  
- `POST /api/metas`: Criar uma nova meta.  
- `PATCH /api/metas/{id}/atingida`: Atualizar status de meta atingida.  
- `GET /api/metas/usuario/{usuarioId}`: Listar metas relacionadas a um usuário.

---

### **Conquistas**
- `GET /api/conquistas/{id}`: Obter detalhes de uma conquista.  
- `PUT /api/conquistas/{id}`: Atualizar conquista existente.  
- `DELETE /api/conquistas/{id}`: Excluir conquista.  
- `POST /api/conquistas`: Criar nova conquista.  
- `GET /api/conquistas/usuario/{usuarioId}`: Listar conquistas relacionadas a um usuário.

---

### **Bandeiras Tarifárias**
- `GET /api/bandeiras-tarifarias/{id}`: Obter detalhes de uma bandeira tarifária.  
- `PUT /api/bandeiras-tarifarias/{id}`: Atualizar bandeira tarifária.  
- `DELETE /api/bandeiras-tarifarias/{id}`: Excluir bandeira tarifária.  
- `GET /api/bandeiras-tarifarias`: Listar todas as bandeiras tarifárias.  
- `POST /api/bandeiras-tarifarias`: Criar uma nova bandeira tarifária.

---

### **Aparelhos**
- `GET /api/aparelhos/{id}`: Obter detalhes de um aparelho.  
- `PUT /api/aparelhos/{id}`: Atualizar dados de um aparelho.  
- `DELETE /api/aparelhos/{id}`: Excluir aparelho.  
- `POST /api/aparelhos/usuario/{usuarioId}`: Criar novo aparelho vinculado a um usuário.  
- `GET /api/aparelhos/{usuarioId}/aparelhos`: Listar aparelhos vinculados a um usuário.

---

### **Autenticação**
- `POST /auth/register`: Registro de novos usuários.  
- `POST /auth/login`: Autenticação e emissão de tokens JWT.

---

### **Recomendações**
- `POST /api/recomendacoes`: Criar nova recomendação personalizada.  
- `GET /api/recomendacoes/usuarios/{usuarioId}`: Listar recomendações associadas a um usuário.

---

### **Notificações**
- `POST /api/notificacoes`: Criar uma nova notificação.  
- `PATCH /api/notificacoes/{id}/lida`: Marcar notificação como lida.  
- `GET /api/notificacoes/{id}`: Obter detalhes de uma notificação.  
- `DELETE /api/notificacoes/{id}`: Excluir notificação.  
- `GET /api/notificacoes/usuario/{usuarioId}`: Listar notificações relacionadas a um usuário.  
- `GET /api/notificacoes/usuario/{usuarioId}/nao-lidas`: Contar notificações não lidas relacionadas a um usuário.

---

## Funcionalidades da Interface MVC

- **Gestão de Aparelhos:** Cadastro, edição, listagem e exclusão.  
- **Monitoramento:** Métricas em tempo real e visualização gráfica do consumo.  
- **Gamificação:** Feedback visual

 das conquistas.  
- **Personalização de Notificações:** Configuração dinâmica com base nos alertas gerados.  

---

## Instalação e Configuração

1. **Clone o repositório:**  
   ```bash
   git clone https://github.com/seu-usuario/prospeco-api.git
   cd prospeco-api
   ```

2. **Configuração do Banco de Dados:**  
   - Crie um banco de dados `prospeco`.  
   - Configure `application.properties` com as credenciais de acesso.  

3. **Execute a Aplicação:**  
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse:**  
   - **MVC Local:** `http://localhost:8080/usuarios`  
   - **Swagger Local:** `http://localhost:8080/swagger-ui.html`  
   - **Swagger Nuvem:** [Swagger UI](https://prospecoapi.azurewebsites.net/swagger-ui/index.html)
   - **MVc Nuvem:** [pagina de usuarios](https://prospecoapi.azurewebsites.net/usuarios)  


---

## Links de Vídeos

1. **Pitch do Projeto:** [Pitch - ProspEco API](https://youtu.be/F9hq-PmF39s)
2. **Demonstração do Sistema:** [Demonstração - ProspEco API](#) *(Adicione o link aqui)*  

---

## Como Contribuir

1. Faça um fork do projeto.  
2. Crie um novo branch:  
   ```bash
   git checkout -b nova-funcionalidade
   ```
3. Faça as alterações e commit:  
   ```bash
   git commit -m "Adiciona nova funcionalidade"
   ```
4. Envie as alterações:  
   ```bash
   git push origin nova-funcionalidade
   ```
5. Abra um Pull Request com os detalhes das alterações realizadas.  

---

## Autores

- **AGATHA PIRES** – RM552247 – (2TDSPH)  
- **DAVID BRYAN VIANA** – RM551236 – (2TDSPM)  
- **GABRIEL LIMA** – RM99743 – (2TDSPM)  
- **GIOVANNA ALVAREZ** – RM98892 – (2TDSPM)  
- **MURILO MATOS** – RM552525 – (2TDSPM)  

Sinta-se à vontade para contribuir com melhorias no **ProspEco API**!
