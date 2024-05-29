Full guide to make the k8s cluster running. Sprung in k8s-config notwendig:

Ggf: minikube delete

Open Docker for Windows.
    minikube start --memory=2048 --cpus=2   \ Mehr Speicher hab ich lokal einfach nicht übrig...

Nervende Warnung weg mit: 
    docker context use default

Secrets einspeisen:

root: 
    kubectl create secret generic mysql-secret --from-literal=password=c8de110f37300a53a971749

webshop:
    kubectl create secret generic webshop-db-secret --from-literal=user=webshopuser
    kubectl create secret generic webshop-db-secret --from-literal=password=240b2c6d58ff2ce2f508b49f
    kubectl create secret generic webshop-db-secret --from-literal=dbname=webshop
        kubectl edit secret webshop-db-secret
  user: d2Vic2hvcHVzZXI=
  password: MjQwYjJjNmQ1OGZmMmNlMmY1MDhiNDlm
  dbname: d2Vic2hvcA==

categories:
    kubectl create secret generic category-db-secret --from-literal=user=categoryUser
    kubectl create secret generic category-db-secret --from-literal=password=74523a732be65aff
    kubectl create secret generic category-db-secret --from-literal=dbname=category
        kubectl edit secret category-db-secret
  user: Y2F0ZWdvcnlVc2Vy
  password: NzQ1MjNhNzMyYmU2NWFmZg==
  dbname: Y2F0ZWdvcnk=
products:
    kubectl create secret generic product-db-secret --from-literal=user=productUser
    kubectl create secret generic product-db-secret --from-literal=password=ae4557bc8900ec
    kubectl create secret generic product-db-secret --from-literal=dbname=product
        kubectl edit secret product-db-secret
  user: cHJvZHVjdFVzZXI=
  password: YWU0NTU3YmM4OTAwZWM=
  dbname: cHJvZHVjdA==

Editieren möglich mit: 
    kubectl edit secret webshop-db-secret
    kubectl edit secret category-db-secret
    kubectl edit secret generic product-db-secret

Eventuell nachkorrektur der secets notwendig da nicht vollständig angelegt. Es müssen aber alle inhalte Bas64 codiert sein!

Config Map mit db info für webshop user einspeisen:
    kubectl apply -f configmap-database.yaml

Datenbank init Config Map ebenfalls ad hoc anlegen, in sql Verzeichnis gehen: 
    kubectl create configmap db-init-scripts --from-file=init.sql=create-tables.sql

Validieren möglich mit 
    kubectl get configmaps 
bzw direkt reingucken mit: 
    kubectl get configmap db-init-scripts -o yaml

NUN; Deployment und Services anwenden aus yaml files.

    kubectl apply -f database-service-deploy.yaml
    kubectl apply -f legacy-webshop-service-deploy.yaml
    kubectl apply -f category-service-deploy.yaml
    kubectl apply -f product-service-deploy.yaml

Direkte Änderungen in db pod per: "kubectl exec -it web-shop-db-image-0 -- mysql -u root -p" mit: c8de110f37300a53a971749 möglich

Example Inserts: 
INSERT INTO category (name) VALUES ('Schreibartikel');

INSERT INTO product (name, details, price, category_id) VALUES ('Bleistift', 'Ein einfacher Bleistift', 1.99, 1);

Wichtige kubernetes Befehle: 
kubectl get pods
kubectl get statefulset
kubectl logs <podname>
kubectl delete pod <podname>

---- Bis hier ist nun Deployment und Services geregelt. Reverse Proxy wird nun eingebaut:

---------------------- Reverse Proxy ----------------------

nginx ingress aktivieren:
    minikube addons enable ingress

in separater shell immer mit admin rechten permanent an: 
    minikube tunnel 

Anwenden von nginx ingress per: 
    kubectl apply -f ingress-ressources.yaml

Wichtige ingress commands: 
    kubectl get pods -n kube-system
    kubectl logs ingress-nginx-controller-84df5799c-h4p7g -n ingress-nginx
    restart ingress 
    kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx
    kubectl describe ingress simple-api-gateway
    kubectl get svc


Anfragen an:
    http://localhost/products/

DNS Debugging:
    kubectl run dns-test --image=busybox --restart=Never -- sleep 3600
    kubectl exec -it dns-test -- nslookup product-service
    kubectl delete pod dns-test

Simples port forwarding für schnellen Test:
    kubectl port-forward svc/product-service 8080:8080 -n default -> http://localhost:8081/products/1

Load Balancer Beweis:
    kubectl scale deployment product-service --replicas=3
    kubectl get pods -l app=product-service
    http://localhost/products/info 3 mal ausführen
        Response from pod: product-service-bbf85455b-5mwhv
        Response from pod: product-service-bbf85455b-q27nx
        Response from pod: product-service-bbf85455b-jz6wx
    kubectl scale deployment product-service --replicas=1

Pod löschen und outcome:
    kubectl get pods
    kubectl delete pod <pod-name>
    kubectl get pods

Gelöschter Pod wird fast unmittelbar ersetzt. Kubernetes hält Anwendung aufrecht.

NUN Istio ---- 

---------------------- ISTIO ----------------------

Aufgrund von Konflikten, den reverse Proxy deaktivieren:
    minikube addons disable ingress

Istio installieren: 
    istioctl install --set profile=demo -y

Namespace für automatische Istio Sidecar Injection konfigurieren:
    kubectl label namespace default istio-injection=enabled

Deployments neu starten, um Istio Sidecars zu injizieren:
    kubectl rollout restart deployment

Prometheus, Grafana und Kiali installieren
cd istio
cd samples
cd addons

kubectl apply -f prometheus.yaml
kubectl apply -f grafana.yaml
kubectl apply -f kiali.yaml

Prometheus Zugriff per port forwading:
    kubectl -n istio-system port-forward svc/prometheus 9090 -> http://localhost:9090

Grafana Zugriff per port forwarding:
    kubectl -n istio-system port-forward svc/grafana 3000 -> http://localhost:3000

Kiali Zugriff per port forwarding:
    kubectl -n istio-system port-forward svc/kiali 20001 -> http://localhost:20001

Validierung ob alles läuft: 
    kubectl get pods -n istio-system
    kubectl get svc -n istio-system
    kubectl get pods,services -n istio-system
    kubectl describe pod <pod-name> -n istio-system
    kubectl describe nodes
    kubectl get nodes -o wide
    kubectl describe node <node-name>
    kubectl describe quota -n istio-system
    kubectl describe limitrange -n istio-system

Validierung der korrekten Zuweisung der Labels im Namespace:
    kubectl get namespace -L istio-injection

Bei Neustart des minikubes folgende Schritte beachten:
    istioctl version -> istio noch da?
    kubectl get namespace default --show-labels -> istio-injection=enabled noch in default? wenn nicht: 
        kubectl label namespace default istio-injection=enabled
    Wenn Änderung, dann: 
        kubectl rollout restart deployment
    Wenn hier: 
        kubectl get pods,services -n istio-system
        Prometheus, Grafana und Kiali fehlen, dann: 
            kubectl apply -f samples/addons/prometheus.yaml
            kubectl apply -f samples/addons/grafana.yaml
            kubectl apply -f samples/addons/kiali.yaml
--------------------------------------------
Wechsel von istio zurück zum reverse Proxy

1. Uninstall istio and delete namespace: 
    istioctl uninstall --purge
    kubectl delete namespace istio-system

2. istio namespace zurück zu default
    kubectl label namespace default istio-injection-

---------------------------------

Zugriff auf den Webshop im k8s Cluster:
http://localhost/EShop-1.0.0/

----------------------------------
Es werden intern andere ip namen verwendet als extern. Beispiel aus: 
legacy webshop yaml file:
angucken über:
    kubectl get svc

Testen mit:
    kubectl run -it --rm --image=curlimages/curl tester -- sh
    # Dann im Shell:
    curl http://product-service:8080/products/
    curl http://category-service:8080/categories/

product-service:8080 ist nur intern bekannt

Neu gebautes image zu dockerhub pushen:
 ✔ Container hska-vis-legacy-web-shop-db-image-1  Running                                                          0.0s
 ✔ Container hska-vis-legacy-product-service-1    Running                                                          0.0s
 ✔ Container hska-vis-legacy-category-service-1   Running                                                          0.0s
 ✔ Container hska-vis-legacy-legacywebshop-1      Started                                                          2.5s
PS > docker login
Authenticating with existing credentials...
Login Succeeded
PS > docker tag hska-vis-legacy-legacywebshop-1 stumpfalexdockerka/hska-vis-legacy-legacywebshop:latest
Error response from daemon: No such image: hska-vis-legacy-legacywebshop-1:latest
PS > docker tag hska-vis-legacy-legacywebshop stumpfalexdockerka/hska-vis-legacy-legacywebshop:latest
PS >

Dann ist es in Docker Desktop sichtbar, und bereit zum pushen
