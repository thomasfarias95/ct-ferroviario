

🥋 Sistema de Gestão - CT Ferroviário de Judô
Sistema full-stack desenvolvido para automatizar a gestão de atletas e o controle financeiro do CT Ferroviário de Judô. O projeto substitui processos manuais por uma interface moderna, com automação de mensalidades e indicadores visuais de inadimplência.

🚀 Funcionalidades
Gestão de Atletas: Cadastro completo de alunos com persistência em banco de dados relacional.

Controle Financeiro Inteligente:

Listagem dinâmica de pagamentos pendentes e realizados.

Automação (Cron Job): Geração automática de mensalidades para todos os atletas no dia 01 de cada mês.

Indicadores Visuais: Destaque automático em vermelho para mensalidades atrasadas.

Dashboard Sensei: Painel com métricas rápidas (Total de Atletas, Alunos em Dia e Inadimplentes).

Segurança: Sistema de login para acesso restrito ao painel administrativo.

🛠️ Tecnologias Utilizadas
Backend
Java 21 com Spring Boot 3

Spring Data JPA (Persistência de dados)

PostgreSQL (Banco de dados relacional)

Spring Scheduling (Automação de tarefas)

Frontend
Next.js 15

TypeScript

Tailwind CSS (Estilização responsiva)

Fetch API (Consumo de API REST)

🏗️ Arquitetura do Banco de Dados
O sistema utiliza um relacionamento de Um-para-Muitos (1:N) entre Atletas e Pagamentos, garantindo a integridade dos dados e o histórico financeiro individual.

🔧 Como Executar o Projeto
Pré-requisitos
JDK 17 ou superior

Node.js 18 ou superior

PostgreSQL rodando localmente
