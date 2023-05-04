#include <windows.h>
#include <winuser.h>
#include <jni.h>

JNIEXPORT jlong JNICALL Java_io_github_solclient_client_util_cursors_Win32SystemCursors_nGetDefaultCursorHandle(
		JNIEnv *env, jclass unused, jbyte cursor) {
	switch (cursor) {
		case 0:
		default:
			return (jlong) LoadCursor(NULL, IDC_ARROW);
		case 1:
			return (jlong) LoadCursor(NULL, IDC_IBEAM);
		case 2:
			return (jlong) LoadCursor(NULL, IDC_CROSS);
		case 3:
			return (jlong) LoadCursor(NULL, IDC_HAND);
		case 4:
			return (jlong) LoadCursor(NULL, IDC_SIZEWE);
		case 5:
			return (jlong) LoadCursor(NULL, IDC_SIZENS);
		case 6:
			return (jlong) LoadCursor(NULL, IDC_SIZENWSE);
		case 7:
			return (jlong) LoadCursor(NULL, IDC_SIZENESW);
		case 8:
			return (jlong) LoadCursor(NULL, IDC_SIZEALL);
		case 9:
			return (jlong) LoadCursor(NULL, IDC_NO);
	}
}

JNIEXPORT void JNICALL Java_io_github_solclient_client_util_cursors_Win32SystemCursors_nSetCursor(
		JNIEnv *env, jclass unused, jlong hwnd, jlong cursor) {
	SetClassLongPtr((HWND) hwnd, GCLP_HCURSOR, NULL);
	SetCursor((HCURSOR) cursor);
}
