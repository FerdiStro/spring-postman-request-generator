# Tool Window 


## Context Settings
![Context_settings.png](img/Context_settings.png)

### Overview
The **Context Settings** panel lets you configure how the plugin connects to your server and application.

### Configuration Options

#### Protocol

| Option     | Description                                               |
|------------|-----------------------------------------------------------|
| `http://`  | Use plain HTTP                                            |
| `https://` | Use HTTPS for secure connections                          |
| `useEnv`   | Resolve protocol from environment variable `{{PROTOCOL}}` |  

---

#### Server

| Option           | Description                                    |
|------------------|------------------------------------------------|
| `localhost:8080` | Default value on first open                    |
| Custom value     | Enter any hostname + port manually             |
| `useEnv`         | Resolve from environment variable `{{SERVER}}` |  

---

#### Application

| Option       | Description                                                 |
|--------------|-------------------------------------------------------------|
| `/api`       | Default value on first open                                 |
| Custom value | Enter any application context path manually                 |
| `useEnv`     | Resolve from environment variable `{{APP_CONTEXT}}`         |
| Disable      | Leave the field empty to disable the application context    |  

---

### Saving Changes
After modifying any settings, click **Save** to persist your changes.  
---