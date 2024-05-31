# How to make kubernetes running in this project

## Necessary Tools
- kubectl
- minikube

## Start k8s cluster
Delete existing cluster: ```minikube delete```    
Start new cluster: ```minikube start --memory=3500 --cpus=4```

## Change docker context:    
```docker context use default```

## Introduce secrets:
- mysql root secret: ```kubectl create secret generic mysql-secret --from-literal=password=c8de110f37300a53a971749```

- webshop secret: ```kubectl create secret generic webshop-db-secret --from-literal=user=webshopuser```     
Execute ```kubectl edit secret webshop-db-secret``` and add
> user: d2Vic2hvcHVzZXI=     
> password: MjQwYjJjNmQ1OGZmMmNlMmY1MDhiNDlm     
> dbname: d2Vic2hvcA==   

------ full base 64 decoded from these credentials     
password=240b2c6d58ff2ce2f508b49f     
dbname=webshop     

- category secret: ```kubectl create secret generic category-db-secret --from-literal=user=categoryUser```      
Execute ```kubectl edit secret category-db-secret``` and add     
> user: Y2F0ZWdvcnlVc2Vy     
> password: NzQ1MjNhNzMyYmU2NWFmZg==    
> dbname: Y2F0ZWdvcnk=     

------ full base 64 decoded from these credentials    
password=74523a732be65aff    
dbname=category    

- product secret: ```kubectl create secret generic product-db-secret --from-literal=user=productUser```     
Execute ```kubectl edit secret product-db-secret``` and add       
> user: cHJvZHVjdFVzZXI=     
> password: YWU0NTU3YmM4OTAwZWM=     
> dbname: cHJvZHVjdA==     

------ full base 64 decoded from these credentials     
password=ae4557bc8900ec     
dbname=product    

Editing possible with, but content needs to be base64 decoded:     
> kubectl edit secret mysql-secret    
> kubectl edit secret webshop-db-secret    
> kubectl edit secret category-db-secret     
> kubectl edit secret generic product-db-secret    

## Insert config maps

Insert config map for database: ```kubectl apply -f configmap-database.yaml```        
 
Create db config map ad hoc and insert with: ```kubectl create configmap db-init-scripts --from-file=init.sql=create-tables.sql```       

Validate with: ```kubectl get configmaps```      
Look into it directly with: ```kubectl get configmap db-init-scripts -o yaml```     

## Service and Deployment 
Apply these files:       
> kubectl apply -f database-service-deploy.yaml     
> kubectl apply -f legacy-webshop-service-deploy.yaml      
> kubectl apply -f category-service-deploy.yaml       
> kubectl apply -f product-service-deploy.yaml      

Execute statements in database pod with: ```kubectl exec -it web-shop-db-image-0 -- mysql -u root -p``` insert: c8de110f37300a53a971749     

Example Inserts:     
```INSERT INTO category (name) VALUES ('Schreibartikel');```
```INSERT INTO product (name, details, price, category_id) VALUES ('Bleistift', 'Ein einfacher Bleistift', 1.99, 1);```

Important k8s commands:        
> kubectl get pods     
> kubectl get statefulset     
> kubectl logs <podname>       
> kubectl delete pod <podname>     

K8s deployment succesfuly achieved

---------------------- Reverse Proxy ----------------------

nginx ingress activate: ```minikube addons enable ingress```     

Open separate shell with admin privileges: ```minikube tunnel```     

Apply ingress config: ```kubectl apply -f ingress-ressources.yaml```     

Important ingress commands:    
> kubectl get pods -n kube-system       
> kubectl logs ingress-nginx-controller-84df5799c-h4p7g -n ingress-nginx      
> restart ingress       
> kubectl rollout restart deployment ingress-nginx-controller -n ingress-nginx      
> kubectl describe ingress simple-api-gateway      
> kubectl get svc      

Product endpoint: ```http://localhost/products/```
Category endpoint: ```http://localhost/categories/```

DNS Debugging:
> kubectl run dns-test --image=busybox --restart=Never -- sleep 3600      
> kubectl exec -it dns-test -- nslookup product-service      
> kubectl delete pod dns-test     

Simple port forwarding for fast testing: ```kubectl port-forward svc/product-service 8080:8080 -n default -> http://localhost:8081/products/1```      

Load Balancer:
> kubectl scale deployment product-service --replicas=3    
> kubectl get pods -l app=product-service      
> http://localhost/products/info       
>> Response from pod: product-service-bbf85455b-5mwhv    
>> Response from pod: product-service-bbf85455b-q27nx     
>> Response from pod: product-service-bbf85455b-jz6wx      

Undo multiple pods: ```kubectl scale deployment product-service --replicas=1```        

Delete pod and outcome:     
> kubectl get pods     
> kubectl delete pod <pod-name>     
> kubectl get pods     

Deleted pod is immediatly being replaced, k8s ensures that the application runs properly

Reverse proxy succesfuly initiated

---------------------- ISTIO ----------------------

deactivate reverse proxy: ```minikube addons disable ingress```     

Install istio: ```istioctl install --set profile=demo -y```

Automatic Istio Sidecar Injection namespace configure: ```kubectl label namespace default istio-injection=enabled```

Restart deployment for istio sidecar injection: ```kubectl rollout restart deployment```

Prometheus, Grafana und Kiali installation: 
> cd istio      
> cd samples      
> cd addons      
> kubectl apply -f prometheus.yaml      
> kubectl apply -f grafana.yaml     
> kubectl apply -f kiali.yaml    

Prometheus access per port forwading:: ```kubectl -n istio-system port-forward svc/prometheus 9090``` -> http://localhost:9090

Grafana access per port forwarding: ```kubectl -n istio-system port-forward svc/grafana 3000``` -> http://localhost:3000

Kiali access per port forwarding: ```kubectl -n istio-system port-forward svc/kiali 20001``` -> http://localhost:20001

Validation if everything is setup correctly: 
> kubectl get pods -n istio-system    
> kubectl get svc -n istio-system     
> kubectl get pods,services -n istio-system      
> kubectl describe pod <pod-name> -n istio-system      
> kubectl describe nodes      
> kubectl get nodes -o wide      
> kubectl describe node <node-name>     
> kubectl describe quota -n istio-system      
> kubectl describe limitrange -n istio-system     

Validierung correct labels in namespace: ```kubectl get namespace -L istio-injection```

When restart minikube do the following:
> istioctl version -> istio noch da?
> kubectl get namespace default --show-labels -> istio-injection=enabled is still set to default? if so: 
>> kubectl label namespace default istio-injection=enabled
>> Immediatly : kubectl rollout restart deployment
    
If: ```kubectl get pods,services -n istio-system```
is missing Prometheus, Grafana and Kiali, then: 
> kubectl apply -f samples/addons/prometheus.yaml
> kubectl apply -f samples/addons/grafana.yaml
> kubectl apply -f samples/addons/kiali.yaml
--------------------------------------------
From istio to reverse proxy

1. Uninstall istio and delete namespace: 
> istioctl uninstall --purge
> kubectl delete namespace istio-system

2. istio namespace zurück zu default
> kubectl label namespace default istio-injection-

---------------------------------
From reverse proxy to istio

> minikube addons disable ingress
> kubectl delete ingress simple-api-gateway
> Validieren das istio load balancer läuft: kubectl get svc istio-ingressgateway -n istio-system
> kubectl apply -f .\istio-proxy-config.yaml

Then the normal istio procedure

---------------------------------

Access webshop in k8s Cluster:
http://localhost/EShop-1.0.0/

----------------------------------

### Allgemeine Feststellungen: 
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
