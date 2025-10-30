#include <IOXhop_FirebaseESP32.h>
#include <IOXhop_FirebaseStream.h> // P/ o Firebase

#include <WiFiManager.h> // Conectividade

#include <ArduinoJson.h> // Arquivos em JSON

#define firLink "https://teste-bd-713b0-default-rtdb.firebaseio.com/"    //Link do BD Firebase (Realtime Database)
#define firPass "x0SDl0TZWhlPCLogMeBEQhf1jzWxsuUBBNMFChI0"   //Pass do BD Firebase
#define sinalSensor 35
#define led 34

char wfSSID[20] = "ESP32_Enchente";
char wfPASS[20] = "Alerta Enchente";

WiFiManager wm; //Variável de conexão

float nivelAnt = -1;

void setup() {
  pinMode(sinalSensor,INPUT);
  pinMode(led, OUTPUT);

  Serial.begin(115200); // Iniciando monitor Serial em 115.200

  bool res; //Variavel de resposta de conexão

  wm.setConnectTimeout(10); // Tempo de espera caso erro na conexão
  wm.setConfigPortalBlocking(false); // Bloqueio durante a conexão cancelado

  res = wm.autoConnect(wfSSID, wfPASS); // Tentativa de conexão

  if(!res) { // "Se não Conectar"
    Serial.println("Falha ao Conectar");
    digitalWrite(led,1);

    Serial.print("Wifi: ");
    Serial.println(wfSSID);
    Serial.print("Senha: ");
    Serial.println(wfPASS);
    Serial.print("IP: ");
    Serial.println(WiFi.softAPIP()); // Conexão do IP interno

    while(WiFi.status() != WL_CONNECTED) { // Enquanto não conectar
      wm.process(); // wm continua a execução
    }
  }
  Serial.println("Conectado!");
  digitalWrite(led,0);
  delay(1000);
  digitalWrite(led,1);
  delay(1000);

  Firebase.begin(firLink, firPass); // Iniciando a conexão com o Firebase

  analogSetPinAttenuation(sinalSensor, ADC_11db); 
  
}

void loop() {
  int leit = 0;
  for(int i = 0; i < 100; i++) {
    leit += analogRead(sinalSensor);
  }
  int leitSensor = leit/100;

  Serial.print("Valor de leitura: ");
  Serial.println(leitSensor);
  float nivel = map(leitSensor, 0, 1000, 0,100);

  Serial.print("Valor de nível: ");
  Serial.println(nivel);

  if(nivel > 100) 
    nivel = 100;

  else if (nivel < 0) 
    nivel = 0;
  

  if(nivel != nivelAnt)
    Firebase.setFloat ("/avPaulista/waterlevel", nivel);
  
  if(nivel >= 50) 
    digitalWrite(led,1);
  else 
    digitalWrite(led,0);

  int a = touchRead(32);
  Serial.println(a);

  if(a < 20) { // Se utilizar o touch
    wm.resetSettings(); // Configurações Reiniciam
    Serial.println("Configurações Reiniciadas");

    bool res = wm.autoConnect(wfSSID, wfPASS); // Tentativa de conexão

    if(!res) { // "Se não Conectar"
      Serial.println("Falha ao Conectar");

      Serial.print("Wifi: ");
      Serial.println(wfSSID);
      Serial.print("Senha: ");
      Serial.println(wfPASS);
      Serial.print("IP: ");
      Serial.println(WiFi.softAPIP()); // Conexão do IP interno

      while(WiFi.status() != WL_CONNECTED) { // Enquanto não conectar
        wm.process(); // wm continua a execução
      }
    }
    Serial.println("Conectado!");
  }
  nivelAnt = nivel;
  
  delay(500);
}