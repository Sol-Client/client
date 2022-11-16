# Wrapper
Wraps the launch and injects functionality of the client.

Usage:
```
java
-Dio.github.solclient.client.mc_version=[Minecraft version]
-Dio.github.solclient.client.version=[client version or "dev"]
-cp
<classpath>
io.github.solclient.client.wrapper.BootstrapMain
[Minecraft args - will be forwarded to Minecraft's main class]
```