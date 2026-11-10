SCRIPT_HOME := ../../scripts

.PHONY: up down smoke create-expense-context delete-expense-context

up:
	orb start k8s
	kubectl config use-context orbstack
	floci start && eval $(floci env)

create-expense-context:
	kubectl config set-context expense --cluster=orbstack --user=orbstack --namespace=expense

delete-expense-context:
	- kubectl config delete-context expense

down:
	floci stop
	orb stop k8s

smoke:
	./${SCRIPT_HOME}/check-tools.sh
	./${SCRIPT_HOME}/floci-smoke.sh