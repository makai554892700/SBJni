# jni 开发(windows inject idea + Clion )
## 环境搭建
* 在[msys官网](https://www.msys2.org/)下载站相应文件安装
* 打开 ming64.exe 执行

        pacman --noconfirm -Syu
        pacman --noconfirm -S mingw-w64-x86_64-gcc mingw-w64-x86_64-cmake
        pacman --noconfirm -S mingw-w64-x86_64-extra-cmake-modules make tar
        pacman --noconfirm -S mingw64/mingw-w64-x86_64-cyrus-sasl
        
* 添加环境变量 
    * %MSYS_HOME%\usr\bin
    * %MSYS_HOME%\mingw64\bin
## 创建调用jni的java类

        package www.mys.com.sbjni.utils;
        
        public class NativeUtils {
        
            static {
                System.loadLibrary("NativeUtils");
            }
        
            public static void main(String[] args) {
                String jniStr = getJniStr();
                System.out.println("jniStr=" + jniStr);
            }
        
            public static native String getJniStr();
        
        }

## 生成相应的class文件和jni头文件，并把头文件移动到native文件夹内

        mkdir src/main/native
        mkdir src/main/native/inc
        mkdir src/main/native/src
        cd src/main/java
        javac www/mys/com/sbjni/utils/NativeUtils.java
        javah www.mys.com.sbjni.utils.NativeUtils
        mv com_mys_com_sbjni_utils_NativeUtils.h ../native/inc/NativeUtils.h
        rm www/mys/com/sbjni/utils/NativeUtils.class

## 在src/main/native/src创建相应.cpp文件

        #include "NativeUtils.h"
        
        JNIEXPORT jstring JNICALL Java_www_mys_com_sbjni_utils_NativeUtils_getJniStr
                (JNIEnv *env, jclass type) {
            return env->NewStringUTF("This is jniStr.");
        }
        
## 在src/main/native创建CMakeLists.txt文件

        cmake_minimum_required(VERSION 3.6)
        project(NativeUtils)
        
        set(CMAKE_CXX_STANDARD 14)
        
        file(GLOB CORE_INCLUDE_DIRS
                ${CMAKE_CURRENT_SOURCE_DIR}/inc
                )
        
        if (CMAKE_SYSTEM_NAME MATCHES "Windows")
            message("system is windows.")
            include_directories(
                    $ENV{JAVA_HOME}/include
                    $ENV{JAVA_HOME}/include/win32
                    ${CORE_INCLUDE_DIRS}
            )
        else ()
            message("system is not windows.")
            include_directories(
                    $ENV{JAVA_HOME}/include
                    $ENV{JAVA_HOME}/include/linux
                    ${CORE_INCLUDE_DIRS}
            )
        endif ()
        
        add_library(NativeUtils SHARED
                src/NativeUtils.cpp
                )
        
        target_link_libraries(NativeUtils)
        
## windows 下打.dll 文件
* Develop PowerShell 下运行

        mkdir build
        cd build
        cmake ..
        MSBuild NativeUtils.sln /t:Rebuild /p:Configuration=Release /p:Platform=x64
        
* 将生成的 Release 下的 NativeUtils.dll 复制至导入resources/libs的路径
## linux 下打.so 文件
* 命令行执行

        mkdir build
        cd build
        cmake ..
        make

* 将生成的native/build/libNativeUtils.so文件复制至导入resources/libs的路径
* 普通运行需要添加 -Djava.library.path=<libs的路径> 参数，但是真实生产环境可能不方便这么处理，因此在InitJob及NativeUtils处理相关。
    * 主要是复制静态/动态库到jar同目录的runLibs下，再将runLibs目录添加至java.library.path环境变量下
    * 最后利用 System.load 加载绝对路径下的静态/动态库
## 更多CMake相关可[参考](https://github.com/makai554892700/CMakeDemo)或者自行百度






















