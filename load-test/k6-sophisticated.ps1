import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const responseTrend = new Trend('response_time');

// Configuration
const BASE_URL = 'http://localhost:8084';
const JWT_TOKEN = 'eyJraWQiOiJIUGZNXC9paXhra1dFWlltTDFyVEtBRDgwMHI3UEU2VExKMmVSR3p4UTBcLzg9IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiI0a3ZhZm52MWFsMzVoYWs1Y280NW9sNnFqNyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiZGVmYXVsdC1tMm0tcmVzb3VyY2Utc2VydmVyLXA3ZWduelwvcmVhZCIsImF1dGhfdGltZSI6MTc1MTgxMzEyNSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LW5vcnRoLTEuYW1hem9uYXdzLmNvbVwvZXUtbm9ydGgtMV9mZzhRemhEa28iLCJleHAiOjE3NTE4MTY3MjUsImlhdCI6MTc1MTgxMzEyNSwidmVyc2lvbiI6MiwianRpIjoiMzE5ODlmOTItNDhmOC00MjAyLTg5ZTctZWVjM2ZkNzg5NTc4IiwiY2xpZW50X2lkIjoiNGt2YWZudjFhbDM1aGFrNWNvNDVvbDZxajcifQ.oOEd322WIGzp9KdAQCuQ5d5spJjpkiZ4vfDHVx5uxB-IMTwt5sy_YEjILvSxUdncu9OEo1khCYguFjOMfLeSbWucmUEVLoLPPw4OiZfvyZTmyzwyls0JdehpE9-SB3A0YMFU7C5knfHO21GTSpuAE5JJZtYVqsxcxHSbLEgumJ_1aJnFT7rSVJQhLz1u2DT0g7el7c2HQu6qAVr0ppGKbpdI2JlTGtCKMTmeGhxRkEC93bKurfJaFY5ZdVqgpNqg_D8yf21itF2gBc6pkO_FHBazkRH4u72UvEu5vq8hlYFx_ldpBH6Zcy5v8Z8JtbdUzrZC7rWnNaP9rJKt_xcT3A';

// Load test options with comprehensive stages
export const options = {
    stages: [
        // Warm-up: Start with 1 user for 30 seconds
        { duration: '30s', target: 1 },
        
        // Ramp-up: Gradually increase to 10 users over 1 minute
        { duration: '1m', target: 10 },
        
        // Normal load: Maintain 10 users for 2 minutes
        { duration: '2m', target: 10 },
        
        // Ramp-up to peak: Increase to 50 users over 2 minutes
        { duration: '2m', target: 50 },
        
        // Peak load: Maintain 50 users for 3 minutes
        { duration: '3m', target: 50 },
        
        // Stress test: Spike to 100 users for 2 minutes
        { duration: '2m', target: 100 },
        
        // Extreme load: Push to 200 users for 1 minute
        { duration: '1m', target: 200 },
        
        // Recovery: Scale back to 50 users
        { duration: '2m', target: 50 },
        
        // Cool-down: Gradually decrease to 10 users
        { duration: '1m', target: 10 },
        
        // Final cool-down: Scale down to 0 users
        { duration: '30s', target: 0 },
    ],
    
    // Performance thresholds
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95% of requests must be below 2s
        http_req_failed: ['rate<0.1'],     // Error rate must be below 10%
        errors: ['rate<0.1'],              // Custom error rate below 10%
        checks: ['rate>0.9'],              // 90% of checks must pass
    },
    
    // Test metadata
    ext: {
        loadimpact: {
            name: 'RabbitMQ Producer Comprehensive Load Test',
            distribution: {
                'amazon:us:ashburn': { loadZone: 'amazon:us:ashburn', percent: 100 },
            },
        },
    },
};

// Test data generator
function generateTestData(vuId, iteration) {
    const scenarios = [
        { type: 'order', priority: 'high' },
        { type: 'user_action', priority: 'medium' },
        { type: 'system_event', priority: 'low' },
        { type: 'notification', priority: 'high' },
    ];
    
    const scenario = scenarios[iteration % scenarios.length];
    
    return {
        data: {
            messageType: scenario.type,
            priority: scenario.priority,
            payload: {
                userId: `user-${vuId}`,
                sessionId: `session-${vuId}-${iteration}`,
                timestamp: new Date().toISOString(),
                data: `Test data for VU ${vuId}, iteration ${iteration}`,
                metadata: {
                    testRun: 'comprehensive-load-test',
                    stage: getCurrentStage(),
                    vuId: vuId,
                    iteration: iteration,
                }
            }
        }
    };
}

// Get current test stage based on execution time
function getCurrentStage() {
    const elapsed = __ENV.K6_EXECUTION_TIME || 0;
    
    if (elapsed < 30) return 'warm-up';
    if (elapsed < 90) return 'ramp-up-light';
    if (elapsed < 210) return 'normal-load';
    if (elapsed < 330) return 'ramp-up-peak';
    if (elapsed < 510) return 'peak-load';
    if (elapsed < 630) return 'stress-test';
    if (elapsed < 690) return 'extreme-load';
    if (elapsed < 810) return 'recovery';
    if (elapsed < 870) return 'cool-down';
    return 'final-cool-down';
}

// Main test function
export default function () {
    const url = `${BASE_URL}/api/data/ingest`;
    const payload = JSON.stringify(generateTestData(__VU, __ITER));
    
    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${JWT_TOKEN}`,
        },
        timeout: '10s', // 10 second timeout
        tags: {
            stage: getCurrentStage(),
            vu: __VU,
        },
    };

    // Make the request
    const startTime = Date.now();
    const res = http.post(url, payload, params);
    const endTime = Date.now();
    
    // Record custom metrics
    responseTrend.add(endTime - startTime);
    errorRate.add(res.status !== 200);
    
    // Perform checks
    const success = check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 2000ms': (r) => r.timings.duration < 2000,
        'response time < 5000ms': (r) => r.timings.duration < 5000,
        'response has body': (r) => r.body && r.body.length > 0,
        'no server errors': (r) => r.status < 500,
    });
    
    // Log errors for debugging
    if (!success || res.status !== 200) {
        console.error(`Request failed - Status: ${res.status}, Body: ${res.body}`);
    }
    
    // Variable sleep based on current stage
    const stage = getCurrentStage();
    switch (stage) {
        case 'warm-up':
            sleep(2); // Slow requests during warm-up
            break;
        case 'extreme-load':
            sleep(0.1); // Minimal sleep during extreme load
            break;
        case 'stress-test':
            sleep(0.2); // Short sleep during stress test
            break;
        default:
            sleep(0.5); // Default sleep
    }
}

// Setup function (runs once before the test)
export function setup() {
    console.log('🚀 Starting comprehensive load test...');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('Test stages: warm-up → ramp-up → normal → peak → stress → extreme → recovery → cool-down');
    
    // Optional: Test connectivity
    const testRes = http.get(`${BASE_URL}/api/status`, {
        timeout: '5s',
    });
    
    if (testRes.status !== 200) {
        console.error('⚠️  Service health check failed. Test may fail.');
    } else {
        console.log('✅ Service health check passed');
    }
    
    return { startTime: Date.now() };
}

// Teardown function (runs once after the test)
export function teardown(data) {
    const duration = (Date.now() - data.startTime) / 1000;
    console.log(`🏁 Test completed in ${duration.toFixed(2)} seconds`);
    console.log('📊 Check the summary below for detailed results');
}