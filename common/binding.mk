include $(COMMON)/docker.mk

test:: $(LIBYAMLSCRIPT_SO_FQNP)

$(LIBYAMLSCRIPT_SO_FQNP): | $(ROOT)/libyamlscript
	$(MAKE) -C $(ROOT)/libyamlscript build

export PATH := $(BUILD_BIN):$(PATH)

build-doc:: build-bin

build-bin:
	$(MAKE) -C $(ROOT) build-bin-ys

clean::
	$(RM) *.tmp

ReadMe.md: $(COMMON)/readme.md $(wildcard doc/*.md) $(ROOT)/util/mdys
	mdys $< > $@.tmp
	mv $@.tmp $@
