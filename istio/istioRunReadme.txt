1. https://github.com/istio/istio/releases -> win exe und src lokal laden und in Ordner
2. istio Ordner zu PATH hinzufügen
3. validieren per: istioctl version : no ready Istio pods in "istio-system / 1.22.0
4. istio installieren: istioctl install --set profile=demo -y
5. Installation validieren: kubectl get crds | findstr 'istio.io'
6. istio aktivieren: kubectl label namespace default istio-injection=enabled
7. deployment neu starten: kubectl rollout restart deployment
8. Aktivieren von port forwarding um auf die istio Tools zuzugreifen:
- kubectl -n istio-system port-forward svc/prometheus 9090 -> http://localhost:9090
- kubectl -n istio-system port-forward svc/grafana 3000 -> http://localhost:3000
- kubectl -n istio-system port-forward svc/kiali 20001 -> http://localhost:20001

Istio uninstall: istioctl x uninstall --purge
Sidecar injection deinstallieren: kubectl label namespace default istio-injection-
Neustart: kubectl rollout restart deployment

Wenn Hardware zu schwach:
minikube delete
minikube start --memory=4096 --cpus=2

This seems to be the norm now (in 2023). You need to create/edit a file in your user profile directory.

cd %UserProfile%
notepad .wslconfig
Then, make sure it has at least something like this:

[wsl2]
memory=6GB   # Limits VM memory in WSL 2 
Reboot your machine and you should have more memory for your containers.



minikube start --vm-driver=hyperv
Habe kein hyperv


