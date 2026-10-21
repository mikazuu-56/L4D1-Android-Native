cmake_minimum_required(VERSION 3.22.1)

project(l4d1_native LANGUAGES CXX)

add_library(
    l4d1_native
    SHARED
    native_engine.cpp
)

target_compile_features(
    l4d1_native
    PRIVATE
    cxx_std_17
)

target_link_libraries(
    l4d1_native
    vulkan
)
