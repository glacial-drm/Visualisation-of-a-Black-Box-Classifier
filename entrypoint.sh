#!/bin/sh

# https://github.com/utensils/docker-opengl

# MIT License

# Copyright (c) 2018 James Brink

# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

if [ $# -eq 0 ]; then
	echo "No command was given to run, exiting."
	exit 1
else
	echo "Starting Xvfb"
	Xvfb :99 -ac -screen 0 "$XVFB_WHD" -nolisten tcp +extension GLX +render -noreset &
	Xvfb_pid="$!"

	echo "Waiting for Xvfb (PID: $Xvfb_pid) to be ready..."
	while ! xdpyinfo -display "${DISPLAY}" > /dev/null 2>&1; do
		sleep 0.1
	done
	echo "Xvfb is running."

	# Execute passed command.
	echo -e "Running command : $@"
	sh -c "$@"
	status=$?

	trap "echo 'Stopping'; kill -SIGTERM $Xvfb_pid" SIGINT SIGTERM
	# Wait for process to end.
	kill $Xvfb_pid
	echo "Waiting for Xvfb (PID: $Xvfb_pid) to shut down..."
	wait $Xvfb_pid

	exit $status
fi
