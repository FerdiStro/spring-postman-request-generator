# PostmanApi

This is a doc for PostmanAPI implemented in the 0.6.0-Version in the Plugin

### Curl-API

Get your API key [here](https://go.postman.co/settings/me/api-keys) to use the cURL commands. Below are all Postman API
requests used in the code as cURL commands:

- `Get` Workspaces

```bash
    curl --location --request GET "https://api.getpostman.com/workspaces" \
    --header "X-Api-Key: PMAK-XXXX"
```

- `Post` to default workspace

```bash
    curl --location --request POST "https://api.getpostman.com/collections" \
    --header "X-Api-Key: <API_TOKEN>" \
    --header "Content-Type: application/json" \
    --data @generated-request.json
```

- `POST` requests apply to the selected workspace only and are available for plan members, not the default workspace.

```bash
    curl --location --request PUT "https://api.getpostman.com/collections?workspace=<WORKSPACE_ID>" \
    --header "X-Api-Key: <API_TOKEN>" \
    --header "Content-Type: application/json" \
    --data @generated-request.json
```

- `GET` List all collections

```bash
    curl --location --request GET "https://api.getpostman.com/collections" \
    --header "X-Api-Key: <API_TOKEN>"
```

- `PUT` Update Collection with collection id

```bash
    curl --location --request PUT "https://api.getpostman.com/collections/<COLLECTION_ID>" \
    --header "X-Api-Key: <API_TOKEN>" \
    --header "Content-Type: application/json" \
    --data @generated-request.json
```