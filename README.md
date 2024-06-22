# project-manager

Projeto voltado para o gerenciamento eficiente de projetos de desenvolvimento de software, integrando-se à API do GitHub para uma experiência mais integrada.

## Tabela de Conteúdos

1. [Instalação](#instalação)
2. [Uso](#uso)


## Instalação

Instruções de instalação serão fornecidas aqui.

## Uso

Demonstração de como usar a plataforma de gerenciamento de projetos.

## Pontos Flexíveis

- Plataforma Git
  - Temos uma interface chamada GitService que deve ser implementada.

- Recomendação de tarefa
  - Será usado um Strategy chamado Recommendation Stategy que deve ser implementado e instanciado no ProjetoService

- Atributos flexíveis de tarefa:
  - Temos uma classe concreta Tarefa que pode ser extendida para criação de novos atributos
  - O banco de dados deve ser atualizado para condizer com os novos atributos
  - A classe abstrata TarefaServiceAbs deve ser Estendida para implementar 3 métodos: Validação, Instanciação, Conversão de Formulário para Tarefa