#include <Xcursor/Xcursor.h>
#include <jni.h>

const char *LOOKUP[] = {
	"default",
	"text",
	"crosshair",
	"pointer",
	"ew-resize",
	"ns-resize",
	"nwse-resize",
	"nesw-resize",
	"all-scroll",
	"not-allowed"
};

JNIEXPORT jlong JNICALL Java_io_github_solclient_client_util_cursors_X11SystemCursors_nGetDefaultCursorHandle(
		JNIEnv *env, jclass unused, jlong display, jbyte cursor) {
	if (cursor < 0 || cursor >= sizeof(LOOKUP) / sizeof(*LOOKUP))
		cursor = 0;

	return XcursorLibraryLoadCursor((Display *) display, LOOKUP[cursor]);
}
