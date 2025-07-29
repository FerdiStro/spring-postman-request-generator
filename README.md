<!-- Plugin description -->

# spring-postman-request-generator

<img width="300" src="https://github.com/FerdiStro/spring-postman-request-generator/raw/main/doc/img/icon.svg" alt="ICON">  
<br>

![Build](https://github.com/FerdiStro/spring-postman-request-generator/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/27997-spring-postman-request-generator)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/27997-spring-postman-request-generator)

A JetBrains IntelliJ plugin that automatically generates [Postman](https://www.postman.com/) collections from Spring
Java classes using `@RequestMapping` annotations.

---

## ✨ Features

* Generates Postman collection files (`.json`) from Spring controller methods
* Supports Spring annotation `@RequestMapping`
* One-click generation via an icon next to your annotated methods
* Output is written to `generated-request.json` in your project root

---

## 🚀 How to Use

1. Open a Spring controller class in IntelliJ.
2. Click the icon next to a method annotated with `@RequestMapping`.
3. A Postman collection will be generated automatically as `generated-request.json` in your project’s base directory.

![RequestMappingInterface.png](https://github.com/FerdiStro/spring-postman-request-generator/raw/main/doc/img/RequestMappingInterface.png)

---

## 📦 Example Output

```json
{
  "info": {
    "name": "Generated Requests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "algos.go",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{PROTOCOL}}{{SERVER}}/{{APP_CONTEXT}}/services/v2/algo/algos.go",
          "host": [
            "{{PROTOCOL}}{{SERVER}}"
          ],
          "path": [
            "{{APP_CONTEXT}}",
            "services",
            "v2",
            "algo",
            "algos.go"
          ],
          "query": []
        }
      },
      "response": []
    }
  ]
}
```

> ✅ This is a valid Postman collection — just import it into Postman and start testing your endpoints.

![postman collection](https://github.com/FerdiStro/spring-postman-request-generator/raw/main/doc/img/PostmanCollection.png)

---

## ⚠️ Notes

* Only methods annotated with Spring Web annotation  `@RequestMapping` is currently supported.
* The plugin is designed for use with Spring projects inside IntelliJ IDEA.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
<!-- Plugin description end -->

---

## 📝 Todo

* [ ] Support all Spring annotations (e.g., `@GetMapping`, `@PostMapping`, etc.)
* [ ] Set up CI/CD pipeline
* [ ] Publish to JetBrains Marketplace
* [ ] Add configuration window for plugin settings

