include ../../common/base.mk
include $(COMMON)/binding.mk
include $(COMMON)/java.mk

RYML_JAVA_JAR := target/ryml-0.5.0.jar

export LD_LIBRARY_PATH := \
  $(ROOT)/rapidyaml/native/rapidyaml-install/lib

#------------------------------------------------------------------------------

x:
	@echo $(LD_LIBRARY_PATH)
	@echo $$LD_LIBRARY_PATH

$(MVN_COMMANDS):: $(JAVA_INSTALLED) $(LIBRYML_SO_FQNP)
	mvn $@

release: $(JAVA_INSTALLED) $(LIBRYML_SO_FQNP) test package
ifndef n
	$(error 'make $@' needs the n variable set to the new version)
endif
	mvn deploy:deploy-file \
	    -s ~/.mvn-user-settings.xml \
	    -Dfile=$(RYML_JAVA_JAR) \
	    -DpomFile=pom.xml \
	    -DrepositoryId=clojars \
	    -Durl=https://clojars.org/repo/

clean::
	$(RM) -r reports/ target/
