
build: rapidyaml
	cmake -S $< -B $<-build \
	    -D BUILD_SHARED_LIBS=OFF \
	    -D RYML_WITH_TAB_TOKENS=ON \
	    -D CMAKE_INSTALL_PREFIX=`pwd`/$<-install
	cmake --build $<-build --parallel --target install


rapidyaml:
	git clone --recursive https://github.com/biojppm/rapidyaml -b newparser
