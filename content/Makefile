.PHONY: all
all: build

.PHONY: build
build: 
	@docker run --rm -it -w /build -v "$$PWD":/build daux/daux.io daux

.PHONY: serve
serve:
	@echo WARNING THIS DOESNT WORK AT THE MOMENT DUE TO A BUG BEING FIXED
	@docker run --rm -it -w /build -v "$$PWD":/build daux/daux.io daux
