#include <jni.h>
#include <vulkan/vulkan.h>
#include <string>
#include <vector>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_l4d1_android_NativeEngine_getStatus(
    JNIEnv* env,
    jclass
) {
    std::string result;

    VkApplicationInfo app{};
    app.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    app.pApplicationName = "L4D1 Android";
    app.apiVersion = VK_API_VERSION_1_0;

    VkInstanceCreateInfo info{};
    info.sType =
        VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    info.pApplicationInfo = &app;

    VkInstance instance = VK_NULL_HANDLE;

    VkResult status = vkCreateInstance(
        &info, nullptr, &instance
    );

    if (status != VK_SUCCESS) {
        result = "Vulkan initialization failed: "
               + std::to_string(status);

        return env->NewStringUTF(result.c_str());
    }

    uint32_t count = 0;

    status = vkEnumeratePhysicalDevices(
        instance, &count, nullptr
    );

    if (status != VK_SUCCESS || count == 0) {
        result = "No Vulkan GPU detected";
    } else {
        std::vector<VkPhysicalDevice> devices(count);

        status = vkEnumeratePhysicalDevices(
            instance, &count, devices.data()
        );

        if (status == VK_SUCCESS ||
            status == VK_INCOMPLETE) {

            for (uint32_t i = 0; i < count; i++) {
                VkPhysicalDeviceProperties props{};

                vkGetPhysicalDeviceProperties(
                    devices[i], &props
                );

                result += "GPU: ";
                result += props.deviceName;

                result += "\nVulkan: ";
                result += std::to_string(
                    VK_VERSION_MAJOR(props.apiVersion)
                );

                result += ".";
                result += std::to_string(
                    VK_VERSION_MINOR(props.apiVersion)
                );

                result += "\nDevice Type: ";
                result += std::to_string(
                    props.deviceType
                );

                result += "\n";
            }
        } else {
            result = "GPU enumeration failed: "
                   + std::to_string(status);
        }
    }

    vkDestroyInstance(instance, nullptr);

    return env->NewStringUTF(result.c_str());
}
