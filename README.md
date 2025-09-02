
# spring-postman-request-generator

<img width="300" src="https://github.com/FerdiStro/spring-postman-request-generator/raw/main/doc/img/icon.svg" alt="ICON">  
<br>

![Build](https://github.com/FerdiStro/spring-postman-request-generator/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/27997-spring-postman-request-generator.svg)](https://plugins.jetbrains.com/plugin/27997-spring-postman-request-generator)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/27997-spring-postman-request-generator.svg)](https://plugins.jetbrains.com/plugin/27997-spring-postman-request-generator)

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
* Plugin is in development, so maybe it will not work as expected

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.


---

## 📝 Todo

* [ ] Support all Spring annotations (e.g., `@GetMapping`, `@PostMapping`, etc.)
* [ ] Set up CI/CD pipeline
* [ ] Publish to JetBrains Marketplace
* [ ] Add configuration window for plugin settings

---
# Plugin Description
<!-- Plugin description -->
**IMPORTANT: Currently under development - functionality still limited and partially buggy. Feel free to contribute and improve the plugin.**

Generate Postman collections directly from your Spring controllers without leaving IntelliJ IDEA.  
This plugin adds a convenient gutter icon next to methods annotated with `@RequestMapping`.  
With a single click, it creates a ready-to-use Postman collection (`generated-request.json`) in your project root.

**Features:**
- One‑click Postman collection generation from Spring `@RequestMapping` methods
- Seamless integration into the IntelliJ editor (gutter icon navigation)
- Exports valid Postman JSON collections ready for import
- Simplifies testing and documentation of your REST endpoints

Perfect for developers who want to quickly test their Spring APIs in Postman without manually creating requests.




<!-- Plugin description end -->
