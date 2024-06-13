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
  - Será usado um Strategy

- Recomendação de tarefa
  - Será usado um Strategy
 
- Atributos customizados da tarefa
  - Será usada uma interface com tipo flexível
  - Essa interface terá um atributo "nome" fixo para cada atributo
  - Terá um atributo "valor" do tipo especificado
  - 
  - Classe Tarefa terá uma array de CustomAttributes
  - Método getCustomAttribute("name") retorna o valor do custom attribute com esse nome definido
  - Teremos um método toJson e um método fromJson em cada CustomAttribute, para persistencia no banco de dados
