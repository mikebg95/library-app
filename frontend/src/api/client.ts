import createClient from 'openapi-fetch';
import type { components, paths } from './schema';

// Generated from backend/src/main/resources/openapi.yaml (npm run generate).
// The contract's servers URL is not part of the paths, so the base is set here.
export const api = createClient<paths>({ baseUrl: '/api/v1' });

export type Loan = components['schemas']['Loan'];
export type Problem = components['schemas']['Problem'];
