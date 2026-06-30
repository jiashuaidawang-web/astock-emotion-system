.PHONY: dev-up dev-down init-mysql init-clickhouse backend frontend build-backend build-frontend check batch package

dev-up:
	bash scripts/dev-up.sh

dev-down:
	bash scripts/dev-down.sh

init-mysql:
	bash scripts/init-mysql.sh

init-clickhouse:
	bash scripts/init-clickhouse.sh

backend:
	bash scripts/run-backend-local.sh

frontend:
	bash scripts/run-frontend-local.sh

build-backend:
	bash scripts/build-backend.sh

build-frontend:
	bash scripts/build-frontend.sh

check:
	bash scripts/check-all.sh

batch:
	bash scripts/run-daily-batch.sh

package:
	bash scripts/package-final.sh
