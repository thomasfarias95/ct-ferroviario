⚙️ CT Ferroviário - API Engine (Spring Boot)
Este repositório contém a inteligência e o motor de persistência do Sistema de Gestão do CT Ferroviário. Uma API REST robusta desenvolvida para centralizar a lógica de negócios, automações financeiras e geração de documentos oficiais para o dojo.

🧠 Diferenciais Técnicos & Lógica de Negócio
O projeto vai além de um CRUD básico, implementando soluções de engenharia para problemas reais de gestão:

📄 Engine de PDF (iText 7): Geração dinâmica de Fichas Técnicas em memória (ByteArrayOutputStream). Os documentos são servidos via streaming de bytes, garantindo performance e segurança sem ocupação de disco no servidor.

⏰ Automação de Mensalidades (Cron Jobs): Implementação de Spring Scheduling para rotinas automáticas todo dia 01, renovando o status financeiro de todos os atletas ativos e gerando novos lançamentos.

⚡ Consultas Otimizadas (Performance): Uso de JOIN FETCH em JPQL para mitigar o problema de N+1 queries, além de Queries Derivadas para buscas de alta performance no banco PostgreSQL.

🔄 Sincronização de Estado: Lógica de serviço que garante a integridade entre a tabela de Pagamentos e o status de exibição no Atleta, resolvendo problemas de inconsistência de dados.

🛠️ Stack Tecnológica
Linguagem: Java 21 (LTS)

Framework: Spring Boot 3.x

Persistência: Spring Data JPA / Hibernate

Banco de Dados: PostgreSQL (Produção no Render)

Gerador de Documentos: iText PDF 7

Gerenciador de Dependências: Maven

<img width="485" height="270" alt="image" src="https://github.com/user-attachments/assets/58a61c23-01b6-4a9c-ae4d-df3fbcbf6ff4" />

🏗️ Arquitetura & Integração
A API foi desenhada seguindo o padrão de camadas (Controller -> Service -> Repository), garantindo que a regra de negócio (como o cálculo de vencimento) fique isolada da exposição dos dados.

CORS: Configurado para permitir integração segura com o Front-end em Next.js.

Transactional: Uso rigoroso de @Transactional para garantir a atomicidade das operações financeiras.

📈 O que vem por aí?
[ ] Integração com API do WhatsApp para notificações de cobrança.

[ ] Dashboards estatísticos de faturamento mensal.

[ ] Autenticação via JWT para múltiplos níveis de acesso (Admin/Professor).


👤 Autor
Thomas Farias Logística & Procurement Professional | Graduando em Ciência da Computação
