# Spring Boot With Istio Demo Project [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

[![CircleCI](https://circleci.com/gh/piomin/sample-istio-services.svg?style=svg)](https://circleci.com/gh/piomin/sample-spring-kotlin-microservice)

In this project I'm demonstrating you the most interesting features of [Istio](https://istio.io) for building service mesh with Spring Boot on Kubernetes.

## Getting Started 
Currently, you may find examples of two Spring Boot applications that may be deployed on Kubernetes. The application caller-service is calling callme-service using Istio rules.
1. The older article about it has been published in 2018, and based on Istio in version 0.8. The example has been moved to the branch [old_master](https://github.com/piomin/sample-istio-services/tree/old_master). Detailed description can be found here: [Service Mesh with Istio on Kubernetes in 5 steps](https://piotrminkowski.com/2018/04/13/service-mesh-with-istio-on-kubernetes-in-5-steps/)
2. The latest example is based on Istio 1.5 and Spring Boot 2.2. The example is available in the branch [master](https://github.com/piomin/sample-istio-services/tree/master). Detailed description can be found here: [Circuit breaker and retries on Kubernetes with Istio and Spring Boot](https://piotrminkowski.com/2020/06/03/circuit-breaker-and-retries-on-kubernetes-with-istio-and-spring-boot/).
3. How to use Telepresence for intercepting traffic in Istio mesh. The example is available in the branch [master](https://github.com/piomin/sample-istio-services/tree/master). Detailed description can be found here: [Development on Kubernetes with Telepresence and Skaffold](https://piotrminkowski.com/2021/12/21/development-on-kubernetes-with-telepresence-and-skaffold/).
