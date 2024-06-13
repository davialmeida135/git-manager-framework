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

 #Sendo testado, talvez vire uma classe concreta e nao uma interface
- Atributos customizados da tarefa
  - Será usada uma interface chamada CustomAttribute com tipo flexível 
  - Essa interface terá um atributo "nome" fixo para cada atributo
  - Terá um atributo "valor" do tipo especificado
  - Métodos getNome, get&setValor
  - Métodos toJson, fromJson
  - 
  - Classe Tarefa terá uma array de CustomAttributes
  - Método getCustomAttribute("name") retorna o valor do custom attribute com esse nome definido
  - On CustomAttribute.java
  ```
  public class CustomAttribute<T> {
    String getName();
    T getValue();
    void setValue(T value);
    String toJson();
    CustomAttribute<T> fromJson();
  }

  
  ```
  - On Tarefa.java
  ```
    private List<CustomAttribute<?>> customAttributes = new ArrayList<>();

    public void addCustomAttribute(CustomAttribute<?> attribute) {
        this.customAttributes.add(attribute);
    }

    public void removeCustomAttribute(CustomAttribute<?> attribute) {
        this.customAttributes.remove(attribute);
    }

    public CustomAttribute<?> getCustomAttribute(String name) {
        return this.customAttributes.stream()
                .filter(attr -> attr.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
  ```
