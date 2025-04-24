# ByteChat 💬

Um simples (e poderoso) chat local em Java, com interface gráfica usando Swing, comunicação via sockets e salvamento de histórico em `.txt`.

---

## 🚀 Funcionalidades

- Chat entre múltiplos usuários
- Interface gráfica amigável com Java Swing
- Escolha de nome de usuário
- Mensagens em tempo real
- Histórico salvo com data/hora
- Base sólida pra evoluir com criptografia, comandos e muito mais!

---

## 📦 Estrutura

ByteChat/
├── src/
│   ├── ChatClientGUI.java
│   └── Server.java
├── chat_history.txt      <-- vai sendo criado durante execução
├── README.md             <-- explicação bonitona



---

## 🧪 Como executar

### ✅ Pré-requisitos:
- Java instalado (JDK 8+)

### 1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/ByteChat.git
cd ByteChat/src
```
### 2. Compile os arquivos:
```bash
javac ChatClientGUI.java Server.java
```
### 3. Rode o servidor:
```bash
java Server
```
### 4. Rode o(s) cliente(s):
- Em outro terminal ou janelas diferentes. E para cada user/processo abra um novo.
```bash
java ChatClientGUI
```

🧠 Aprendizados nesse projeto

Comunicação cliente-servidor usando sockets TCP

Manipulação de threads para múltiplas conexões

Criação de GUI com Java Swing

Troca de mensagens entre processos

Registro de logs com timestamps em arquivo .txt


🛠 Próximos passos

 Adicionar criptografia nas mensagens

 Sistema de autenticação e login

 Melhorias na interface com layout mais moderno

 Suporte para comandos especiais (ex: /w, /clear)

 Comunicação em redes externas (via IP)



