
# PubSub Tester

Esta aplicação tem como objetivo auxiliar desenvolvedores a realizar testes locais integrando com o Google PubSub. Ela permite conectar-se a um emulador PubSub local para gerenciá-lo ou interagir com outras aplicações. As funcionalidades disponíveis são:

- **Criar tópico**
```
curl --location --request POST 'http://localhost:8080/v1/topic' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "seu-topico"
}'
```

- **Criar subscrição**
```
curl --location --request POST 'http://localhost:8080/v1/subscription/seu-topico' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "sua-subscricao"
}'
```

- **Publicar mensagem em um tópico**
A aplicação aceita qualquer texto/JSON como payload. Esse payload será publicado exatamente como recebido no tópico informado.
```
curl --location --request POST 'http://localhost:8080/v1/topic/publish/seu-topico' \
--header 'Content-Type: application/json' \
--data-raw '{
    "atributo": "valor"
}'
```

- **Consumir mensagens de uma subscrição**
A aplicação retornará todas as mensagens que conseguir consumir em um período de 3 segundos, apresentando uma lista de objetos contendo o ID da mensagem e o conteúdo.
```
curl --location --request GET 'http://localhost:8080/v1/subscription/receive/sua-subscricao'
```

Para utiliza-la, basta estar executando um emulador PubSub e ajustar as configurações de project-id e emulator-host conforme o seu emulador.

## Instalando e executando um emulador PubSub
Para utilizar o emulador PubSub, você deve ter o Google Cloud SDK (gcloud) instalado em sua máquina. Caso não possua o gcloud, siga as instruções disponíveis [aqui](https://cloud.google.com/sdk/docs/install?hl=pt-br).

Após instalar e configurar o gcloud, abra o terminal e execute os seguintes passos:

1. Instale o BETA:
```
gcloud components install beta
```

2. Instale o emulador PubSub:
```
gcloud components install pubsub-emulator
```

3. Atualize os componentes instalados:
```
gcloud components update
```

Se você encontrar problemas ao executar comandos devido a erros de SSL, siga estas etapas:

1. Desabilite a validação SSL:
```
gcloud config set auth/disable_ssl_validation true
```

2. Atualize os componentes do gcloud:
```
gcloud components update
```

### Executando o emulador
Agora que você ja possui o emulador instalado, para executa-lo basta executar o comando:
```
gcloud beta emulators pubsub start --project=seu-project-id
```

## Executando a aplicação
Agora que o emulador está instalado e em execução, ajuste as variáveis project-id e emulator-host no arquivo application.yml e execute a aplicação.

