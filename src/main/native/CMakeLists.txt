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

