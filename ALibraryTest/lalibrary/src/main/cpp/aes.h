#ifndef HEADER_AES_H
#define HEADER_AES_H

#include <stddef.h>

#define AES_ENCRYPT	1
#define AES_DECRYPT	0

#define AES_MAXNR 14
#define AES_BLOCK_SIZE 16

struct aes_key_st
{
#ifdef AES_LONG
	unsigned long rd_key[4 *(AES_MAXNR + 1)];
#else
	unsigned int rd_key[4 * (AES_MAXNR + 1)];
#endif
	int rounds;
};
typedef struct aes_key_st AES_KEY;

void bin2Hex(char *bin, size_t size, char *asc, size_t asc_size);
int hex2bin(const char *pSrc, unsigned char *pDst, int nSrcLength);
int AES_set_encrypt_key(const unsigned char *userKey, const int bits,
		AES_KEY *key);
int AES_set_decrypt_key(const unsigned char *userKey, const int bits,
		AES_KEY *key);

void AES_encrypt(const unsigned char *in, unsigned char *out,
		const AES_KEY *key);
void AES_decrypt(const unsigned char *in, unsigned char *out,
		const AES_KEY *key);

void AES_ecb_encrypt(const unsigned char *in, unsigned char *out,
		const AES_KEY *key, const int enc);

int aes_ecb_encrypt_PKCS5Padding(char * in, int len, char * out, char * key, int keyBit);
int aes_ecb_decrypt_PKCS5Padding(char* in, int len, char* out, char* key, int keyBit);
#endif /* !HEADER_AES_H */
