cmake_minimum_required(VERSION 3.20)
project(game_of_life_v2_0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_PREFIX_PATH "C:/Qt/6.2.1/mingw81_64/lib/cmake")

find_package(Qt6Widgets REQUIRED)

set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTORCC ON)

add_executable(game_of_life_v2_0 main.cpp engine.cpp renderarea.cpp renderarea.h gamewindow.cpp gamewindow.h game.cpp game.h io.cpp io.h rulesdialog.cpp rulesdialog.h sizedialog.cpp sizedialog.h Ifield.h)
target_link_libraries(game_of_life_v2_0 Qt6::Widgets)
