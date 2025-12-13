echo "🔍 Testing backend network accessibility..."

echo "1. Testing localhost..."
curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:8080/api/test/echo

echo ""
echo "2. Testing 127.0.0.1..."
curl -s -o /dev/null -w "Status: %{http_code}\n" http://127.0.0.1:8080/api/test/echo

echo ""
echo "3. Testing 0.0.0.0..."
curl -s -o /dev/null -w "Status: %{http_code}\n" http://0.0.0.0:8080/api/test/echo

echo ""
echo "4. Testing your Mac IP (192.168.18.56)..."
curl -s -o /dev/null -w "Status: %{http_code}\n" http://192.168.18.56:8080/api/test/echo

echo ""
echo "5. Testing Android emulator IP (10.0.2.2)..."
timeout 5 curl -s -o /dev/null -w "Status: %{http_code}\n" http://10.0.2.2:8080/api/test/echo || echo "Status: Failed to connect"
